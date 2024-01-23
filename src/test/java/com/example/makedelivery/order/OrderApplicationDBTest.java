package com.example.makedelivery.order;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.member.model.CartResponse;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.service.CartService;
import com.example.makedelivery.domain.member.service.MemberService;
import com.example.makedelivery.domain.order.domain.OrderMenuOptionRequest;
import com.example.makedelivery.domain.order.domain.OrderMenuRequest;
import com.example.makedelivery.domain.order.domain.OrderResponse;
import com.example.makedelivery.domain.order.domain.entity.Order;
import com.example.makedelivery.domain.order.domain.entity.OrderMenu;
import com.example.makedelivery.domain.order.domain.entity.Payment;
import com.example.makedelivery.domain.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.makedelivery.common.exception.ExceptionEnum.INVALID_POINT_UNIT;
import static com.example.makedelivery.common.exception.ExceptionEnum.ORDER_CANCEL_ERROR;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class OrderApplicationDBTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    private Member member;
    private Member owner;
    private List<OrderMenuRequest> orderMenuRequestList;

    private int testPrice;

    @BeforeEach
    void setMember() {

        member = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr"); // Member Test ID
        owner = memberService.findMemberByEmail("bTestAdmin710a@admin.co.kr"); // Owner Test ID

        orderMenuRequestList = new ArrayList<>();
        List<OrderMenuOptionRequest> optionRequestList = new ArrayList<>();
        OrderMenuOptionRequest optionRequest = OrderMenuOptionRequest.builder()
                .price(1_000)
                .menuOptionId(1L)
                .build();
        optionRequestList.add(optionRequest);
        OrderMenuRequest orderMenuRequest1 = OrderMenuRequest.builder()
                .cartId(1L)
                .price(20_000)
                .menuId(1L)
                .count(2)
                .optionList(optionRequestList)
                .build();
        OrderMenuRequest orderMenuRequest2 = OrderMenuRequest.builder()
                .cartId(2L)
                .price(15_000)
                .menuId(2L)
                .count(1)
                .optionList(new ArrayList<>()) // empty
                .build();
        orderMenuRequestList.add(orderMenuRequest1);
        orderMenuRequestList.add(orderMenuRequest2);

        testPrice = (20_000 * 2) + 15_000 + 1_000;

    }

    @Test
    @DisplayName("회원은 원하는 메뉴를 주문할 수 있고, 주문 후 조회 할 수 있습니다. 카트는 비워져 있습니다. " +
                 "그리고 할인 포인트를 사용하지 않았을 시,1%포인트가 적립됩니다. - Redis 에는 내 주문 정보가 캐싱되어 담깁니다.")
    @Transactional
    void registerOrderTest() {
        // when
        final int BEFORE_POINT = member.getPoint();
        orderService.registerOrder(member, orderMenuRequestList, Payment.PaymentType.KAKAO_PAY,
                1L, 1L, 0);
        // then ( order )
        List<OrderResponse> myOrderResponseList = orderService.getMyOrderList(member);
        for ( int i=0; i<myOrderResponseList.size(); i++ ) {
            System.out.println("주문정보 : " + myOrderResponseList.get(i).toString());
        }
        // then ( cart )
        List<CartResponse> cartResponseList = cartService.loadCart(member);
        for ( int i=0; i<cartResponseList.size(); i++ ) {
            System.out.println("내 장바구니 : " + cartResponseList.get(i).toString());
        }
        // then ( Point )
        System.out.println("적립 후 포인트 : " + member.getPoint());
        assertThat(BEFORE_POINT + (testPrice/100)).isEqualTo(member.getPoint());
    }

    @Test
    @DisplayName("포인트를 사용하여 주문을 하면, 포인트가 차감되고, 그만큼 할인하여 주문을 할 수 있습니다." +
                 "또한 포인트를 사용하면, 1%적립은 생기지 않습니다.")
    @Transactional
    void registerOrderUsePointTest() {
        // when
        final int BEFORE_POINT = member.getPoint();
        final int BEFORE_AVAIL_POINT = member.getAvailablePoint();
        final int USE_POINT = 5_000;
        orderService.registerOrder(member, orderMenuRequestList, Payment.PaymentType.KAKAO_PAY,
                1L, 1L, USE_POINT);
        // then ( order )
        List<OrderResponse> myOrderResponseList = orderService.getMyOrderList(member);
        for ( int i=0; i<myOrderResponseList.size(); i++ ) {
            System.out.println("주문정보 : " + myOrderResponseList.get(i).toString());
        }
        // then ( cart )
        List<CartResponse> cartResponseList = cartService.loadCart(member);
        for ( int i=0; i<cartResponseList.size(); i++ ) {
            System.out.println("내 장바구니 : " + cartResponseList.get(i).toString());
        }
        // then ( Point )
        System.out.println("적립 후 포인트 ( 적립 되지 않음 ) : " + member.getPoint());
        System.out.println("차감 후 사용가능 포인트 : " + member.getAvailablePoint());
        assertThat(BEFORE_POINT + (testPrice/100)).isNotEqualTo(member.getPoint());
        assertThat(BEFORE_AVAIL_POINT - USE_POINT).isEqualTo(member.getAvailablePoint());
    }


    @Test
    @DisplayName("매장 정주는 본인 매장에 들어온 주문 리스트를 확인 할 수 있습니다.")
    @Transactional
    void getMyStoreOrderListTest() {
        // when
        orderService.registerOrder(member, orderMenuRequestList, Payment.PaymentType.KAKAO_PAY,
                1L, 1L, 0);
        // then ( order )
        List<OrderResponse> myOrderResponseList = orderService.getMyStoreOrderList(owner, 1L);
        for ( int i=0; i<myOrderResponseList.size(); i++ ) {
            System.out.println("주문정보 : " + myOrderResponseList.get(i).toString());
        }
    }

    @Test
    @DisplayName("주문 완료, 주문 승인 의 경우에는 주문 취소를 할 수 있습니다.")
    @Transactional
    void cancelOrderByMemberTest() {
        // when
        orderService.registerOrder(member, orderMenuRequestList, Payment.PaymentType.KAKAO_PAY,
                1L, 1L, 0);
        List<OrderResponse> myOrderResponseList = orderService.getMyOrderList(member);
        Long orderId = myOrderResponseList.get(0).getId();
        orderService.cancelOrderByMember(member, orderId, 1L);
        // then
        myOrderResponseList = orderService.getMyOrderList(member);
        assertThat(Order.OrderStatus.CANCEL_ORDER.toString()).isEqualTo(myOrderResponseList.get(0).getOrderStatus());
    }

    @Test
    @DisplayName("주문 완료, 주문 승인 이외의 경우에는 주문 취소를 할 수 없습니다.")
    @Transactional
    void notCancelOrderByMemberTest() {
        // when
        orderService.registerOrder(member, orderMenuRequestList, Payment.PaymentType.KAKAO_PAY,
                1L, 1L, 0);
        List<OrderResponse> myOrderResponseList = orderService.getMyOrderList(member);
        Long orderId = myOrderResponseList.get(0).getId();
        orderService.changeOrderStatusDeliveryWait(owner, orderId, 1L);
        // then
        ApiException apiException = assertThrows(ApiException.class, () -> {
            orderService.cancelOrderByMember(member, orderId, 1L);
        });
        assertEquals(ORDER_CANCEL_ERROR.getCode(), apiException.getError().getCode());
    }
}
