package com.delivery.optimizer.repository;

import com.delivery.optimizer.model.DeliveryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository

public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, Long>{


    List<DeliveryHistory> findByDayOfWeek(DayOfWeek  dayOfWeek);

    List<DeliveryHistory> findTop500ByOrderByDeliveryDateDesc();
}