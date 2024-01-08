package com.example.makedelivery.domain.order.service;

public interface PaymentService {

    void payment(long orderId, long memberId, int price);

}
