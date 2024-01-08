package com.example.makedelivery.domain.order.service;

import com.example.makedelivery.common.utils.PaymentServiceFactory;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.order.domain.OrderMenuOptionRequest;
import com.example.makedelivery.domain.order.domain.OrderMenuRequest;
import com.example.makedelivery.domain.order.domain.entity.Order;
import com.example.makedelivery.domain.order.domain.entity.OrderMenu;
import com.example.makedelivery.domain.order.domain.entity.OrderMenuOption;
import com.example.makedelivery.domain.order.domain.entity.Payment;
import com.example.makedelivery.domain.order.domain.entity.Payment.PaymentType;
import com.example.makedelivery.domain.order.repository.OrderMenuOptionRepository;
import com.example.makedelivery.domain.order.repository.OrderMenuRepository;
import com.example.makedelivery.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.makedelivery.domain.order.domain.entity.Order.OrderStatus.COMPLETE_ORDER;

/**
 * OrderTransactionService 를 새롭게 만든 이유는, Self-Invocation 때문입니다.
 * Transactional 어노테이션은 AOP 로 구현되어있기 때문에 프록시 객체를 생성합니다.
 * 그래서, 예를들어 A라는 메서드 안에서 같은 서비스의 Transactional 이 붙은 메서드를 호출하면,
 * 프록시 객체의 메서드가 아닌, 원래 객체의 메서드를 호출하기 때문에 ( This )
 * Transactional 이 적용되지 않습니다.
 * 물론 현재는, 주문과 결제가 같은 트랜잭션에서 처리되야 하므로
 * 굳이 OrderTransactionService 를 만들어서 트랜잭션을 분리할 필요는 없지만
 * 추후에 전파레벨이 변경되거나 ( ex. @Transactional(propagation = Propagation.REQUIRES_NEW) )
 * 주문과 결제가 같은 트랜잭션에서 처리되지 않게 변경해야 할 요소등을 고려하여, 분리를 하였습니다.
 * <br><br>
 * 또한, 현재 전파레벨을 기본 값으로 한 이유는 주문 트랜잭션 안에서
 * 주문과 결제 모든 것이 하나의 트랜잭션 단위로 처리되어야 하기 때문입니다. ( Commit, Rollback 이 한 번에 이루어져야 합니다 .)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderTransactionService {

    private final PaymentServiceFactory paymentServiceFactory;

    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final OrderMenuOptionRepository orderMenuOptionRepository;

    @Transactional
    public Long registerOrder(Member member, List<OrderMenuRequest> requestList, Long addressId,
                              Long storeId, int totalPrice, int usePoint, int actualPrice) {

        Order order = Order.builder()
                .addressId(addressId)
                .memberId(member.getId())
                .storeId(storeId)
                .originalPrice(totalPrice)
                .discount(usePoint)
                .actualPrice(actualPrice)
                .orderStatus(COMPLETE_ORDER)
                .build();

        // 주문 저장
        Long orderId = orderRepository.save(order).getId();

        // 주문 메뉴 저장
        requestList.forEach(request -> {
            Long orderMenuId = orderMenuRepository.save(OrderMenuRequest.toEntity(request, orderId)).getId();

            // 주문 메뉴 옵션 저장
            saveOrderMenuOptions(request.getOptionList(), orderMenuId);
        });

        return orderId;

    }

    @Transactional
    public void payment(PaymentType payType, Long orderId, Long memberId, int actualPrice) {

        PaymentService paymentService = paymentServiceFactory.getPaymentService(payType);
        paymentService.payment(orderId, memberId, actualPrice);

    }

    private void saveOrderMenuOptions(List<OrderMenuOptionRequest> optionList, Long orderMenuId) {
        optionList.stream()
                .map(optionRequest -> OrderMenuOptionRequest.toEntity(optionRequest, orderMenuId))
                .forEach(orderMenuOptionRepository::save);
    }

}
