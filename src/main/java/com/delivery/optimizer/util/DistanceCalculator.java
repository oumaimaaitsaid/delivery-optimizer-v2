package com.delivery.optimizer.util;

import com.delivery.optimizer.model.Delivery;
import com.delivery.optimizer.model.Warehouse;
import org.springframework.stereotype.Component;


@Component
public class DistanceCalculator {
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }


    //  2. Distance entre entrepôt et livraison
    public double calculateDistance(Warehouse w, Delivery d) {
        return calculateDistance(w.getLatitude(), w.getLongitude(),
                d.getLatitude(), d.getLongitude());
    }

    // 3. Distance entre deux livraisons
    public double calculateDistance(Delivery d1, Delivery d2) {
        return calculateDistance(d1.getLatitude(), d1.getLongitude(),
                d2.getLatitude(), d2.getLongitude());
    }
}
