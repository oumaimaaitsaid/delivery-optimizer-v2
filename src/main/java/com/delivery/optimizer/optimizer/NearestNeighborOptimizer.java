package com.delivery.optimizer.optimizer;

import com.delivery.optimizer.model.Delivery;
import com.delivery.optimizer.model.Warehouse;
import com.delivery.optimizer.util.DistanceCalculator;


import java.util.*;
@Component
public class NearestNeighborOptimizer implements TourOptimizer{
    private final DistanceCalculator distanceCalculator;

    public NearestNeighborOptimizer() {
        this(new DistanceCalculator());
    }

    public NearestNeighborOptimizer(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }
    @Override
    public List<Delivery> calculateOptimalTour(Warehouse warehouse, List<Delivery> deliveries) {
        List<Delivery> remaining = new ArrayList<>(deliveries);
        List<Delivery> result= new ArrayList<>();


        double currentLat = warehouse.getLatitude();
        double currentLon = warehouse.getLongitude();

        while (!remaining.isEmpty()) {
            Delivery nearest=null;
            double minDistance =Double.MAX_VALUE;

            for(Delivery d : remaining){
                double distance = distanceCalculator.calculateDistance(currentLat,currentLon,d.getLatitude(),d.getLongitude());
                if(distance<minDistance){

                    minDistance=distance;
                    nearest=d;
                }
            }
            result.add(nearest);
            currentLon=nearest.getLongitude();
            currentLat=nearest.getLatitude();
            remaining.remove(nearest);
        }
return result;

    }

}
