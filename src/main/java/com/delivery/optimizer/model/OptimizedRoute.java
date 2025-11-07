package com.delivery.optimizer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimizedRoute {

    private List<OrderedDelivery> orderedDeliveries;

    private String recommendations;
    private PredictedBestRouteMetrics predictedBestRouteMetrics;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderedDelivery {
        private Long id;
        private String predictedStartTime;
        private String justification;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PredictedBestRouteMetrics {
        private Integer totalTravelTimeMinutes;
        private Integer averageDelayImprovementPercentage;
    }
}