package com.delivery.optimizer.repository;

import com.delivery.optimizer.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
}



