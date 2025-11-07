package com.delivery.optimizer.service;

import com.delivery.optimizer.model.Delivery;
import com.delivery.optimizer.model.DeliveryStatus;
import com.delivery.optimizer.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime; // N'ti9nou men import

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryHistoryService historyService; // Injection dial l'historique


    public Delivery updateDeliveryStatus(Long deliveryId, DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery non trouvée"));

        delivery.setStatus(newStatus);

        // --- LOGIQUE V2 : CRÉATION DE L'HISTORIQUE ---
        if (newStatus == DeliveryStatus.COMPLETED) {
            delivery.setActualTime(LocalTime.now());
            historyService.createHistoryFromCompletedDelivery(delivery);
        }

        return deliveryRepository.save(delivery);
    }
}