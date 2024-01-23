package com.example.makedelivery.domain.order.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.model.entity.MemberAddress;
import com.example.makedelivery.domain.member.service.CartService;
import com.example.makedelivery.domain.member.service.MemberAddressService;
import com.example.makedelivery.domain.menu.domain.entity.Menu;
import com.example.makedelivery.domain.menu.domain.entity.Option;
import com.example.makedelivery.domain.menu.service.MenuService;
import com.example.makedelivery.domain.menu.service.OptionService;
import com.example.makedelivery.domain.order.domain.*;
import com.example.makedelivery.domain.order.domain.entity.Order;
import com.example.makedelivery.domain.order.domain.entity.Payment;
import com.example.makedelivery.domain.order.repository.OrderMenuOptionRepository;
import com.example.makedelivery.domain.order.repository.OrderMenuRepository;
import com.example.makedelivery.domain.order.repository.OrderRepository;
import com.example.makedelivery.domain.store.model.entity.Store;
import com.example.makedelivery.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.example.makedelivery.common.constants.CacheValueConstants.*;
import static com.example.makedelivery.common.exception.ExceptionEnum.*;

/**
 * OrderService 에 Delete Method 가 없는 이유.
 * 주문 취소나, 배송이 완료된 정보도 추후 배송 리스트에서
 * 다시 확인 할 수 있게 지원해주어야 하며
 * 하나의 주문 데이터를 Member 와 Owner 가 함께 보기 때문에,
 * 한 쪽에서 일방적으로 삭제할 수 없습니다.
 * 그래서 삭제 로직은 추후 재설계를 하고, 우선 개발하지 않았습니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderTransactionService orderTransactionService; // self-invocation 을 해결하기 위한 Service
    private final CartService cartService;
    private final StoreService storeService;
    private final MenuService menuService;
    private final OptionService optionService;
    private final MemberAddressService memberAddressService;

    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final OrderMenuOptionRepository orderMenuOptionRepository;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = ORDER_LIST_BY_MEMBER, key = "'member:' + #member.id", cacheManager = "redisCacheManager"),
            @CacheEvict(value = CART_LIST, key = "'member:' + #member.id", cacheManager = "redisCacheManager"),
            @CacheEvict(value = ORDER_LIST_BY_STORE, key = "'store:' + #storeId", cacheManager = "redisCacheManager")
    })
    public void registerOrder(Member member, List<OrderMenuRequest> requestList, Payment.PaymentType payType,
                              Long addressId, Long storeId, Integer usePoint) {

        // 해당 매장이 Deleted 상태인지 확인 ( Delete 상태라면, 장바구니를 비웁니다. )
        validateOrderStoreCheck(member, storeId);

        // Validation 체크
        for ( OrderMenuRequest request : requestList ) {
            validateMenu(request, member);
        }

        // 총 구매 금액
        int totalPrice = calculateTotalPrice(requestList);

        // 구매 가격보다, 사용 포인트가 많을 수 없습니다.
        validateUsePoint(member, totalPrice, usePoint);

        // 실제 결제 금액
        int actualPrice = calculateActualPrice(totalPrice, usePoint);

        // 포인트 할인을 받지 않았고, 5,000원 이상 구매하였다면, 총 구매의 1%만큼 적립 포인트 추가.
        addPoint(member, totalPrice, usePoint);

        // 결제 완료 후 주문 테이블에 담기
        Long orderId = orderTransactionService.registerOrder(member, requestList, addressId, storeId, totalPrice, usePoint, actualPrice);

        // 문제 없으면 결제
        orderTransactionService.payment(payType, orderId, member.getId(), actualPrice);
        
        // 결제가 완료되면, 장바구니를 비웁니다.
        // 장바구니에 든 걸 전체적으로 구매하도록 설계하였기 때문에, 장바구니 전체를 비워줍니다.
        cartService.deleteCartAll(member);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = ORDER_LIST_BY_MEMBER, key = "'member:' + #member.id", cacheManager = "redisCacheManager"),
            @CacheEvict(value = ORDER_LIST_BY_STORE, key = "'store:' + #storeId", cacheManager = "redisCacheManager")
    })
    public void cancelOrderByMember(Member member, Long orderId, Long storeId) {
        Order order = orderRepository.findOrderByIdAndMemberId(orderId, member.getId())
                .orElseThrow( () -> new ApiException(ORDER_NOT_FOUND) );

        // 주문 완료, 주문 승인 의 경우에만 주문 취소를 할 수 있습니다.
        Order.OrderStatus orderStatus = order.getOrderStatus();
        if ( orderStatus.equals(Order.OrderStatus.COMPLETE_ORDER) || orderStatus.equals(Order.OrderStatus.APPROVED_ORDER) ) {
            order.cancelOrder();
            orderTransactionService.cancelPayment(orderId, member.getId()); // 결제 상태도 취소가 됩니다.
        } else {
            throw new ApiException(ORDER_CANCEL_ERROR);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = ORDER_LIST_BY_MEMBER, key = "'member:' + #member.id", cacheManager = "redisCacheManager"),
            @CacheEvict(value = ORDER_LIST_BY_STORE, key = "'store:' + #storeId", cacheManager = "redisCacheManager")
    })
    public void cancelOrderByStore(Member member, Long orderId, Long storeId) {
        storeService.validationCheckedMyStore(storeId, member); // 매장 점주 맞는지 체크
        Order order = orderRepository.findOrderByIdAndStoreId(orderId, storeId)
                .orElseThrow( () -> new ApiException(ORDER_NOT_FOUND) );

        // 주문 완료, 주문 승인, 배송 대기 중 의 경우에만 주문 취소를 할 수 있습니다.
        Order.OrderStatus orderStatus = order.getOrderStatus();
        if ( orderStatus.equals(Order.OrderStatus.COMPLETE_ORDER) || orderStatus.equals(Order.OrderStatus.APPROVED_ORDER) || orderStatus.equals(Order.OrderStatus.DELIVERY_WAIT) ) {
            order.cancelOrder();
            orderTransactionService.cancelPayment(orderId, member.getId()); // 결제 상태도 취소가 됩니다.
        } else {
            throw new ApiException(ORDER_CANCEL_ERROR);
        }
    }

    @Cacheable(key = "'member:' + #member.id", value = ORDER_LIST_BY_MEMBER, cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrderList(Member member) {
        return orderRepository.findAllByMemberIdOrderByUpdateDateTimeDesc(member.getId())
                .orElse(List.of())
                .stream()
                .map(order -> {
                    List<OrderMenuResponse> orderMenuResponseList = orderMenuRepository.findAllByOrderId(order.getId())
                            .orElse(List.of())
                            .stream()
                            .map(orderMenu -> {
                                List<OrderMenuOptionResponse> optionResponseList = orderMenuOptionRepository.findAllByOrderMenuId(orderMenu.getId())
                                        .orElse(List.of())
                                        .stream()
                                        .map(orderMenuOption -> {
                                            Option option = optionService.findOptionById(orderMenuOption.getOptionId());
                                            return OrderMenuOptionResponse.toOrderMenuOptionResponse(orderMenuOption, option.getName());
                                        })
                                        .toList();

                                Menu menu = menuService.getMenuById(orderMenu.getMenuId());
                                return OrderMenuResponse.toOrderMenuResponse(orderMenu, menu.getName(), optionResponseList);
                            })
                            .toList();

                    MemberAddress memberAddress = memberAddressService.findMemberAddressByIdAndMemberId(order.getAddressId(), order.getMemberId());
                    Store store = storeService.findThisStore(order.getStoreId());
                    return OrderResponse.toOrderResponse(order, memberAddress.getAddress(), memberAddress.getLongitude(), memberAddress.getLatitude(), store.getName(), orderMenuResponseList);
                })
                .toList();
    }

    @Cacheable(value = ORDER_LIST_BY_STORE, key = "'store:' + #storeId", cacheManager = "redisCacheManager")
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyStoreOrderList(Member member, Long storeId) {
        storeService.validationCheckedMyStore(storeId, member); // 매장 점주 맞는지 체크
        return orderRepository.findAllByStoreIdOrderByUpdateDateTimeDesc(storeId)
                .orElse(List.of())
                .stream()
                .map(order -> {
                    List<OrderMenuResponse> orderMenuResponseList = orderMenuRepository.findAllByOrderId(order.getId())
                            .orElse(List.of())
                            .stream()
                            .map(orderMenu -> {
                                List<OrderMenuOptionResponse> optionResponseList = orderMenuOptionRepository.findAllByOrderMenuId(orderMenu.getId())
                                        .orElse(List.of())
                                        .stream()
                                        .map(orderMenuOption -> {
                                            Option option = optionService.findOptionById(orderMenuOption.getOptionId());
                                            return OrderMenuOptionResponse.toOrderMenuOptionResponse(orderMenuOption, option.getName());
                                        })
                                        .toList();

                                Menu menu = menuService.getMenuById(orderMenu.getMenuId());
                                return OrderMenuResponse.toOrderMenuResponse(orderMenu, menu.getName(), optionResponseList);
                            })
                            .toList();

                    MemberAddress memberAddress = memberAddressService.findMemberAddressByIdAndMemberId(order.getAddressId(), order.getMemberId());
                    Store store = storeService.findThisStore(order.getStoreId());
                    return OrderResponse.toOrderResponse(order, memberAddress.getAddress(), memberAddress.getLongitude(), memberAddress.getLatitude(), store.getName(), orderMenuResponseList);
                })
                .toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = ORDER_LIST_BY_MEMBER, key = "'member:' + #member.id", cacheManager = "redisCacheManager"),
            @CacheEvict(value = ORDER_LIST_BY_STORE, key = "'store:' + #storeId", cacheManager = "redisCacheManager")
    })
    public void changeOrderStatusApprove(Member member, Long orderId, Long storeId) {
        storeService.validationCheckedMyStore(storeId, member); // 매장 점주 맞는지 체크
        Order order = orderRepository.findOrderByIdAndStoreId(orderId, storeId)
                .orElseThrow( () -> new ApiException(ORDER_NOT_FOUND) );

        order.changeOrderStatusApprove();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = ORDER_LIST_BY_MEMBER, key = "'member:' + #member.id", cacheManager = "redisCacheManager"),
            @CacheEvict(value = ORDER_LIST_BY_STORE, key = "'store:' + #storeId", cacheManager = "redisCacheManager")
    })
    public void changeOrderStatusDeliveryWait(Member member, Long orderId, Long storeId) {
        storeService.validationCheckedMyStore(storeId, member); // 매장 점주 맞는지 체크
        Order order = orderRepository.findOrderByIdAndStoreId(orderId, storeId)
                .orElseThrow( () -> new ApiException(ORDER_NOT_FOUND) );

        order.changeOrderStatusDeliveryWait();
    }

    private void validateOrderStoreCheck(Member member, Long storeId) {
        Store store = storeService.findThisStore(storeId);
        // 현재 매장 Status 상태 확인
        if (store.isDeletedCheck()) {
            cartService.deleteCartAll(member); // 장바구니에서 삭제
            throw new ApiException(STORE_DELETED);
        }
        // 현재 매장 Open & Close 상태 확인
        if (store.isClosedCheck()) {
            throw new ApiException(STORE_CLOSED);
        }
    }

    private void validateMenu(OrderMenuRequest request, Member member) {
        Menu menu = menuService.getMenuById(request.getMenuId());
        if (menu.isDeletedCheck()) {
            cartService.deleteCart(member, request.getCartId());
            throw new ApiException(MENU_DELETED);
        }
        if (menu.isHiddenCheck()) {
            throw new ApiException(MENU_HIDDEN);
        }
        if (!Objects.equals(menu.getPrice(), request.getPrice())) {
            throw new ApiException(MENU_PRICE_NOT_MATCHED);
        }
        for ( OrderMenuOptionRequest optionRequest : request.getOptionList() ) {
            validateMenuOption(optionRequest);
        }
    }

    private void validateMenuOption(OrderMenuOptionRequest request) {
        Option option = optionService.findOptionById(request.getMenuOptionId());
        // 옵션 상태 확인
        if ( option.isDeletedCheck() ) {
            throw new ApiException(OPTN_DELETED);
        }
        // 옵션 상태 확인
        if ( option.isHiddenCheck() ) {
            throw new ApiException(OPTN_HIDDEN);
        }
        // 가격 변동이 있을 수 있으니 실제 옵션 가격 vs Cart 에서 넘어온 옵션 가격 비교
        if ( !Objects.equals(option.getPrice(), request.getPrice()) ) {
            throw new ApiException(OPTN_PRICE_NOT_MATCHED);
        }
    }

    private void validateUsePoint(Member member, int totalPrice, int usePoint) {
        if ( member.getAvailablePoint() < usePoint ) throw new ApiException(LACK_PONT); // 실제 포인트 부족
        if ( totalPrice < usePoint ) throw new ApiException(OVER_POINT);
    }

    private int calculateTotalPrice(List<OrderMenuRequest> requestList) {
        return requestList.stream()
                .mapToInt(request ->
                        request.getPrice() * request.getCount() +
                                calculateOptionPrices(request.getOptionList()))
                .sum();
    }

    private int calculateOptionPrices(List<OrderMenuOptionRequest> optionList) {
        return optionList.stream()
                .mapToInt(OrderMenuOptionRequest::getPrice)
                .sum();
    }

    private int calculateActualPrice(int totalPrice, int usePoint) {
        if ( totalPrice < usePoint ) throw new ApiException(OVER_POINT);
        return totalPrice - usePoint;
    }

    private void addPoint(Member member, int totalPrice, int usePoint) {
        if ( usePoint == 0 && totalPrice >= 5_000 ) {
            member.addPoint( totalPrice / 100 );
        }
    }
}
