package com.example.makedelivery.domain.rider.repository;

import com.example.makedelivery.domain.rider.domain.entity.RiderDeliveryHistory;
import com.example.makedelivery.domain.rider.domain.entity.RiderDeliveryHistory.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RiderRepository extends JpaRepository<RiderDeliveryHistory, Long> {

    Optional<RiderDeliveryHistory> findRiderDeliveryHistoryByIdAndRiderId(Long id, Long riderId);

    void deleteRiderDeliveryHistoryByIdAndRiderId(Long id, Long riderId);

    Optional<List<RiderDeliveryHistory>> findAllByRiderIdAndDeliveryStatus(Long riderId, DeliveryStatus deliveryStatus);

}
