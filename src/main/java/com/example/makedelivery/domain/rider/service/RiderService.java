package com.example.makedelivery.domain.rider.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.common.exception.ExceptionEnum;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.repository.MemberAddressRepository;
import com.example.makedelivery.domain.order.domain.entity.Order;
import com.example.makedelivery.domain.order.repository.OrderRepository;
import com.example.makedelivery.domain.rider.domain.DeliveryHistoryResponse;
import com.example.makedelivery.domain.rider.domain.RiderPossibleOrderResponse;
import com.example.makedelivery.domain.rider.domain.entity.RiderDeliveryHistory;
import com.example.makedelivery.domain.rider.domain.entity.RiderDeliveryHistory.DeliveryStatus;
import com.example.makedelivery.domain.rider.repository.RiderRepository;
import com.example.makedelivery.domain.store.model.entity.Store;
import com.example.makedelivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.makedelivery.common.exception.ExceptionEnum.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RiderService {

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final RiderRepository riderRepository;
    private final MemberAddressRepository memberAddressRepository;


    @Transactional(readOnly = true)
    public List<RiderPossibleOrderResponse> getRiderPossibleOrder(Member member, Double myLatitude, Double myLongitude) {
        return storeRepository.findAllWithInDistanceOrderByDistance(myLatitude, myLongitude)
                .orElse(List.of())
                .stream()
                .flatMap(store -> orderRepository.findAllByStoreIdAndOrderStatus(store.getId(), Order.OrderStatus.DELIVERY_WAIT)
                        .orElse(List.of())
                        .stream()
                        .map(order -> {
                            String deliveryAddress = memberAddressRepository.findMemberAddressesById(order.getAddressId())
                                    .orElseThrow( () -> new ApiException(ADDR_NOT_FOUND))
                                    .getAddress();
                            return RiderPossibleOrderResponse.toRiderPossibleOrderResponse(order.getId(), store.getId(), store.getName(), deliveryAddress);
                        }))
                .toList();
    }

    @Transactional
    public void registerRider(Member member, Long orderId) {

        Order order = orderRepository.findOrderById(orderId).orElseThrow( () -> new ApiException(ORDER_NOT_FOUND) );

        if ( order.getRiderId() != null ) throw new ApiException(ALREADY_DISPATCHED); // 이미 배차 완료 시 Exception

        order.registerRider(member.getId());

        String deliveryAddress = memberAddressRepository.findMemberAddressesById(order.getAddressId())
                .orElseThrow( () -> new ApiException(ADDR_NOT_FOUND))
                .getAddress();

        RiderDeliveryHistory riderDeliveryHistory = RiderDeliveryHistory.builder()
                .riderId(member.getId())
                .orderId(orderId)
                .deliveryStatus(DeliveryStatus.DELIVERING)
                .deliveryAddress(deliveryAddress)
                .build();

        riderRepository.save(riderDeliveryHistory);
    }

    @Transactional
    public void cancelRider(Member member, Long deliveryId) {
        RiderDeliveryHistory riderDeliveryHistory = riderRepository.findRiderDeliveryHistoryByIdAndRiderId(deliveryId, member.getId())
                .orElseThrow( () -> new ApiException(DELIV_NOT_FOUND));

        Order order = orderRepository.findOrderById(riderDeliveryHistory.getOrderId())
                .orElseThrow( () -> new ApiException(ORDER_NOT_FOUND) );

        order.cancelRider(); // 주문 테이블에서는 다시 배송 대기로 변경
        riderRepository.deleteRiderDeliveryHistoryByIdAndRiderId(deliveryId, member.getId()); // 배달 History 에서는 아예 삭제

    }

    @Transactional
    public void completedDelivery(Member member, Long deliveryId) {
        RiderDeliveryHistory riderDeliveryHistory = riderRepository.findRiderDeliveryHistoryByIdAndRiderId(deliveryId, member.getId())
                .orElseThrow( () -> new ApiException(DELIV_NOT_FOUND));

        Order order = orderRepository.findOrderById(riderDeliveryHistory.getOrderId())
                .orElseThrow( () -> new ApiException(ORDER_NOT_FOUND) );

        order.completedDelivery();
        riderDeliveryHistory.completedDelivery();

    }

    @Transactional(readOnly = true)
    public List<DeliveryHistoryResponse> getMyDeliveryHistory(Member member, DeliveryStatus status) {
        return riderRepository.findAllByRiderIdAndDeliveryStatus(member.getId(), status)
                .orElse(Collections.emptyList())
                .stream()
                .map(riderDeliveryHistory ->
                        DeliveryHistoryResponse.toDeliveryHistoryResponse(
                                riderDeliveryHistory.getId(),
                                riderDeliveryHistory.getOrderId(),
                                riderDeliveryHistory.getDeliveryAddress()
                        )
                )
                .toList();
    }

}
