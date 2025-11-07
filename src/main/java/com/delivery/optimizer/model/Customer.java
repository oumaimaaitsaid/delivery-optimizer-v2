package com.delivery.optimizer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List; // Ajout pour la collection

@Entity
@Table(name = "customer") // Correction de la faute de frappe et convention
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer { // Correction de la faute de frappe
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Exigence : un nom est requis
    private String address;
    private double latitude;
    private double longitude;
    private String preferredTimeSlot;

    // RELATION : Un Customer peut avoir plusieurs DeliveryHistory
    // mappedBy = "customer" indique que c'est le champ 'customer' dans l'entité DeliveryHistory qui gère la relation.
    // fetch = Lazy est la bonne pratique pour les collections.
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DeliveryHistory> deliveryHistories;

    // Note : Si V1 utilise une entité 'Delivery', vous devriez également avoir une relation pour les 'Delivery' en cours.
    // J'utilise ici 'DeliveryHistory' car c'est la nouvelle entité que nous créons.
}