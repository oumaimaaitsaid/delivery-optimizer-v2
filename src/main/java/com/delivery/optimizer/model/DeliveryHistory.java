package com.delivery.optimizer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;

@Entity
@Table(name = "delivery_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELATION : Chaque historique est associé à UN Customer (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Référence au Tour (l'entité V1) qui a généré cet historique.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    private LocalDate deliveryDate;

    // Pour l'analyse de l'IA, il est utile d'enregistrer l'adresse réelle de la livraison.
    private String actualDeliveryAddress;
    private double actualLatitude;
    private double actualLongitude;

    // Temps d'arrivée/départ Prévu et Réel
    private LocalTime plannedTime;
    private LocalTime actualTime;

    // Le délai (actualTime - plannedTime)
    private Long delayInMinutes;
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

}