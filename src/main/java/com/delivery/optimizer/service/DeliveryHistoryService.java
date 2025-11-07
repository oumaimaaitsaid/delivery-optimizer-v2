package com.delivery.optimizer.service;

import com.delivery.optimizer.model.Delivery;
import com.delivery.optimizer.model.DeliveryHistory;
import com.delivery.optimizer.repository.DeliveryHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor // bach ndirou Injection dial Dépendances b'sra3
public class DeliveryHistoryService {

    private final DeliveryHistoryRepository historyRepository;

    /**
     * Kikhlaq enregistrement dial l'historique fach ka tssali wahed la livraison.
     */
    public void createHistoryFromCompletedDelivery(Delivery delivery) {


        if (delivery.getActualTime() == null || delivery.getPlannedTime() == null) {
            System.err.println("ERREUR: Impossible de créer l'historique, temps manquant pour la livraison ID: " + delivery.getId());
            return;
        }

        DeliveryHistory history = new DeliveryHistory();

        // 1. Références (snapshot les données)
        history.setCustomer(delivery.getCustomer());

        // 2. Calculer le délai
        long delayInMinutes = ChronoUnit.MINUTES.between(delivery.getPlannedTime(), delivery.getActualTime());

        // 3. Importer les données
        history.setDeliveryDate(delivery.getDate());
        history.setPlannedTime(delivery.getPlannedTime());
        history.setActualTime(delivery.getActualTime());
        history.setDelayInMinutes(delayInMinutes);
        history.setDayOfWeek(delivery.getDate().getDayOfWeek());

        // 4. Données géographiques pour l'IA
        history.setActualLatitude(delivery.getLatitude());
        history.setActualLongitude(delivery.getLongitude());
        history.setActualDeliveryAddress(delivery.getAddress());

        historyRepository.save(history);
    }
}