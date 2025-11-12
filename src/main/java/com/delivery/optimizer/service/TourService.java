package com.delivery.optimizer.service;

import com.delivery.optimizer.model.Delivery;
import com.delivery.optimizer.model.Warehouse;
import com.delivery.optimizer.optimizer.TourOptimizer;
import com.delivery.optimizer.util.DistanceCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
public class TourService {
    private final TourOptimizer optimizer;
   private  final DistanceCalculator  distanceCalculator;
    private static final Logger log = LoggerFactory.getLogger(TourService.class);
    public TourService(TourOptimizer optimizer,DistanceCalculator distanceCalculator) {
        this.optimizer =optimizer;
        this.distanceCalculator = distanceCalculator;
    }

    public List<Delivery> getOptimizedTour(Warehouse warehouse,List<Delivery>  deliveries){
        return optimizer.calculateOptimalTour(warehouse,deliveries);
    }


    public List<Delivery> calculateOptimalTour(Warehouse warehouse, List<Delivery> deliveries) {
        log.info("Appel de l'optimizer: {}", optimizer.getClass().getSimpleName());
        // Howa ghir kaydewwez l khdma l l'optimizer l s7i7 (AI)
        return optimizer.calculateOptimalTour(warehouse, deliveries);
    }
    public double getTotalDistance(Warehouse warehouse,List<Delivery> orderedDeliveries){
        if(orderedDeliveries == null||orderedDeliveries.isEmpty()) return 0.0;
        double totalDistance = 0.0;
        double currentLat = warehouse.getLatitude();
        double currentLon = warehouse.getLongitude();

        for(Delivery d :orderedDeliveries){
            totalDistance += distanceCalculator.calculateDistance(currentLat,currentLon,d.getLatitude(),d.getLongitude());
            currentLat = d.getLatitude();
            currentLon = d.getLongitude();
        }

        totalDistance += distanceCalculator.calculateDistance(currentLat, currentLon, warehouse.getLatitude(), warehouse.getLongitude());
        log.info("[DIST] routeSize={}, totalDistance={}", orderedDeliveries.size(), totalDistance);
        return   totalDistance;
    }
}
