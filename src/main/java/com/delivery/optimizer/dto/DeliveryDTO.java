package com.delivery.optimizer.dto;

import com.delivery.optimizer.model.DeliveryStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DeliveryDTO {
    private Long id;
    private double latitude;
    private double longitude;
    private double weight;
    private double volume;
    private String timeWindow;
    private DeliveryStatus status;
    private String address;
    private String preferredTimeSlot;
    private Long tourId;
    private Long customerId;
    private LocalTime plannedTime;
    private LocalDate date;

}
