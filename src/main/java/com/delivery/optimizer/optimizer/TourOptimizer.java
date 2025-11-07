package com.delivery.optimizer.optimizer;

import com.delivery.optimizer.model.Delivery;
import com.delivery.optimizer.model.OptimizedRoute;
import com.delivery.optimizer.model.Warehouse;

import java.util.List;

public interface TourOptimizer {

    OptimizedRoute optimize(Warehouse warehouse, List<Delivery> deliveries);
}
