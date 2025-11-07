package com.delivery.optimizer.repository;

import com.delivery.optimizer.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

}
