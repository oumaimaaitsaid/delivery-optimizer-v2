package com.delivery.optimizer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data; // Ghadi nbeddlo hada
import lombok.NoArgsConstructor;
import java.util.List;

// --- Imports jdad li ghadi n7tajo ---
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import java.util.ArrayList; // Hada mohim bzzaf

@Entity
@Table(name = "customer")
// --- 1. L CORRECTION DIAL L MOCHKIL 2 ---
// Kan7eyydo @Data o kandiro dakchi li bghina b tefsil
// bach n7ebso l "boucle infinie" dial @ToString
@Getter
@Setter
@EqualsAndHashCode(exclude = "deliveryHistories") // Kanqolo lih maysta3melch had l lista f l equals
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String preferredTimeSlot;

    // RELATION :
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)

    // --- 2. L CORRECTION DIAL L MOCHKIL 1 (L MOHIM BZAF) ---
    // Dima khass l collection tkon m-initialisée b liste khawya bach n éviter l NullPointerException
    private List<DeliveryHistory> deliveryHistories = new ArrayList<>();

}