package com.delivery.optimizer.repository;
import org.springframework.stereotype.Repository;
import com.delivery.optimizer.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import com.delivery.optimizer.model.DeliveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {


    List<Delivery> findByStatusOrderByPlannedTimeAsc(DeliveryStatus status);


    @Query("SELECT d FROM Delivery d WHERE d.customer.id = :customerId")
    Page<Delivery> findByCustomerIdWithPagination(
            @Param("customerId") Long customerId,
            Pageable pageable
    );

}
