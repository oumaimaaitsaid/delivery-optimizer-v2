package com.delivery.optimizer.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DELIVERY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;
    private double longitude;
    private double weight;
    private double volume;
    private String timeWindow;
    private String address;
    private String preferredTimeSlot;

    @Enumerated(EnumType.STRING)
    private  DeliveryStatus status;

    @ManyToOne
    @JoinColumn(name ="tour_id")
    private Tour tour;

}
