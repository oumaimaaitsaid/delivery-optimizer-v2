package com.delivery.optimizer.repository;
import org.springframework.stereotype.Repository;
import com.delivery.optimizer.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}
