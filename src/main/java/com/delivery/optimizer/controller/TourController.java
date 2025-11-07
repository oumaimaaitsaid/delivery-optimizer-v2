package com.delivery.optimizer.controller;

import com.delivery.optimizer.dto.TourDTO;
import com.delivery.optimizer.mapper.TourMapper;
import com.delivery.optimizer.model.Tour;
import com.delivery.optimizer.model.Delivery;
import com.delivery.optimizer.model.Vehicle;
import com.delivery.optimizer.model.Warehouse;
import com.delivery.optimizer.optimizer.ClarkeWrightOptimizer;
import com.delivery.optimizer.optimizer.TourOptimizer;
import com.delivery.optimizer.repository.DeliveryRepository;
import com.delivery.optimizer.repository.TourRepository;
import com.delivery.optimizer.repository.VehicleRepository;
import com.delivery.optimizer.repository.WarehouseRepository;
import com.delivery.optimizer.service.TourService;
import com.delivery.optimizer.util.DistanceCalculator;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.delivery.optimizer.dto.CompareRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tours")
public class TourController {

    private final TourRepository tourRepository;
    private final VehicleRepository vehicleRepository;
    private final WarehouseRepository warehouseRepository;
    private final DeliveryRepository deliveryRepository;
    private final TourService tourService;

    private static final Logger log = LoggerFactory.getLogger(TourController.class);

    public TourController(TourRepository tourRepository,
                          VehicleRepository vehicleRepository,
                          WarehouseRepository warehouseRepository,
                          DeliveryRepository deliveryRepository,
                          TourService tourService) {
        this.tourRepository = tourRepository;
        this.vehicleRepository = vehicleRepository;
        this.warehouseRepository = warehouseRepository;
        this.deliveryRepository = deliveryRepository;
        this.tourService = tourService;
    }

    @GetMapping
    public List<TourDTO> getTours() {
        return tourRepository.findAll()
                .stream()
                .map(TourMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TourDTO getTourById(@PathVariable Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        return TourMapper.toDTO(tour);
    }

    @PostMapping
    public TourDTO createTour(@RequestBody TourDTO dto) {
        Tour tour = TourMapper.toEntity(dto);
        if (dto.getVehicleId() != null) {
            tour.setVehicle(vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found")));
        }
        if (dto.getWarehouseId() != null) {
            tour.setWarehouse(warehouseRepository.findById(dto.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found")));
        }
        Tour saved = tourRepository.save(tour);
        return TourMapper.toDTO(saved);
    }

    @PutMapping("/{id}")
    public TourDTO update(@PathVariable Long id, @RequestBody TourDTO dto) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        tour.setDate(dto.getDate());
        if (dto.getDeliveryIds() != null) {
            tour.setVehicle(vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found")));
        }
        if (dto.getWarehouseId() != null) {
            tour.setWarehouse(warehouseRepository.findById(dto.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found")));
        }
        Tour updated = tourRepository.save(tour);
        return TourMapper.toDTO(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        tourRepository.deleteById(id);
    }

    @PostMapping("/compare")
    public Map<String, Object> compare(@RequestBody CompareRequest req) {
        try {
            if (req == null || req.getWarehouseId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "warehouseId est requis");
            }
            if (req.getDeliveryIds() == null || req.getDeliveryIds().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "deliveryIds est requis et ne doit pas être vide");
            }

            log.info("[COMPARE] warehouseId={}, vehicleId={}, deliveryCount={}", req.getWarehouseId(), req.getVehicleId(), req.getDeliveryIds().size());

            Warehouse warehouse = warehouseRepository.findById(req.getWarehouseId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Warehouse not found"));
            Vehicle vehicle = null;
            if (req.getVehicleId() != null) {
                vehicle = vehicleRepository.findById(req.getVehicleId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));
            }

            List<Delivery> deliveries = deliveryRepository.findAllById(req.getDeliveryIds());
            if (deliveries.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucune delivery trouvée pour les IDs fournis");
            }

            DistanceCalculator dc = new DistanceCalculator();
            TourOptimizer cw = new ClarkeWrightOptimizer(dc);

            long t1 = System.nanoTime();
            List<Delivery> cwRoute = cw.calculateOptimalTour(warehouse, deliveries);
            double cwDistance = tourService.getTotalDistance(warehouse, cwRoute);
            long t2 = System.nanoTime();

            long t3 = System.nanoTime();
            List<Delivery> nnRoute = nearestNeighbor(warehouse, deliveries, dc);
            double nnDistance = tourService.getTotalDistance(warehouse, nnRoute);
            long t4 = System.nanoTime();

            Map<String, Object> res = new HashMap<>();
            Map<String, Object> cwRes = new HashMap<>();
            cwRes.put("orderedDeliveryIds", cwRoute.stream().map(Delivery::getId).collect(Collectors.toList()));
            cwRes.put("totalDistance", cwDistance);
            cwRes.put("durationMs", (t2 - t1) / 1_000_000.0);

            Map<String, Object> nnRes = new HashMap<>();
            nnRes.put("orderedDeliveryIds", nnRoute.stream().map(Delivery::getId).collect(Collectors.toList()));
            nnRes.put("totalDistance", nnDistance);
            nnRes.put("durationMs", (t4 - t3) / 1_000_000.0);

            res.put("clarkeWright", cwRes);
            res.put("nearestNeighbor", nnRes);
            String winner;
            if (cwDistance < nnDistance) winner = "CLARKE_WRIGHT";
            else if (nnDistance < cwDistance) winner = "NEAREST_NEIGHBOR";
            else winner = ((t2 - t1) <= (t4 - t3)) ? "CLARKE_WRIGHT" : "NEAREST_NEIGHBOR";
            res.put("winner", winner);

            log.info("[COMPARE] CW dist={}ms={}, NN dist={} ms={}, winner={}",
                    cwDistance, String.format("%.3f", (t2 - t1) / 1_000_000.0),
                    nnDistance, String.format("%.3f", (t4 - t3) / 1_000_000.0),
                    winner);
            return res;
        } catch (ResponseStatusException ex) {
            log.warn("[COMPARE] {} {} - {}", HttpStatus.valueOf(ex.getStatusCode().value()), ex.getStatusCode(), ex.getReason());
            throw ex;
        } catch (Exception ex) {
            log.error("[COMPARE] Erreur inattendue", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur");
        }
    }

    private List<Delivery> nearestNeighbor(Warehouse warehouse, List<Delivery> deliveries, DistanceCalculator dc) {
        List<Delivery> remaining = new ArrayList<>(deliveries);
        List<Delivery> route = new ArrayList<>();
        double curLat = warehouse.getLatitude();
        double curLon = warehouse.getLongitude();
        while (!remaining.isEmpty()) {
            Delivery best = null;
            double bestDist = Double.MAX_VALUE;
            for (Delivery d : remaining) {
                double dist = dc.calculateDistance(curLat, curLon, d.getLatitude(), d.getLongitude());
                if (dist < bestDist) {
                    bestDist = dist;
                    best = d;
                }
            }
            route.add(best);
            curLat = best.getLatitude();
            curLon = best.getLongitude();
            remaining.remove(best);
        }
        return route;
    }
}
