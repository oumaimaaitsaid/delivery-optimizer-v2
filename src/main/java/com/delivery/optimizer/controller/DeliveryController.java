package com.delivery.optimizer.controller;
import com.delivery.optimizer.repository.CustomerRepository;
import com.delivery.optimizer.dto.DeliveryDTO;
import com.delivery.optimizer.mapper.DeliveryMapper;
import com.delivery.optimizer.model.Delivery;
import com.delivery.optimizer.model.DeliveryStatus;
import com.delivery.optimizer.repository.DeliveryRepository;
import com.delivery.optimizer.repository.TourRepository;
import com.delivery.optimizer.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    private final DeliveryRepository deliveryRepository;
    private final TourRepository tourRepository;
    private final CustomerRepository customerRepository;
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryRepository deliveryRepository , TourRepository tourRepository,CustomerRepository customerRepository,DeliveryService deliveryService)
    {
        this.deliveryRepository=deliveryRepository;
        this.tourRepository = tourRepository;
        this.customerRepository = customerRepository;
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public List<DeliveryDTO> getAll() {
        return deliveryRepository.findAll()
                .stream()
                .map(DeliveryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DeliveryDTO getById(@PathVariable Long id) {
       Delivery delivery= deliveryRepository.findById(id).orElseThrow(()->new RuntimeException("delivery not found"));
       return DeliveryMapper.toDTO(delivery);
    }

    @PostMapping
    public DeliveryDTO create(@RequestBody DeliveryDTO dto) {

        Delivery delivery = DeliveryMapper.toEntity(dto);

        if (dto.getTourId() != null) {
            delivery.setTour(tourRepository.findById(dto.getTourId())
                    .orElseThrow(() -> new RuntimeException("Tour not found")));
        }
        if (dto.getCustomerId() == null) {
            throw new RuntimeException("Customer ID is required"); // 7ssen tqol lihom
        }

        delivery.setCustomer(customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found")));


        Delivery saved = deliveryRepository.save(delivery);
        return DeliveryMapper.toDTO(saved);
    }


    @PutMapping("/{id}")
    public DeliveryDTO update(@PathVariable Long id, @RequestBody DeliveryDTO dto) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setAddress(dto.getAddress());
        delivery.setLatitude(dto.getLatitude());
        delivery.setLongitude(dto.getLongitude());
        delivery.setWeight(dto.getWeight());
        delivery.setVolume(dto.getVolume());

        delivery.setPreferredTimeSlot(dto.getPreferredTimeSlot());

        if (dto.getTourId() != null) {
            delivery.setTour(tourRepository.findById(dto.getTourId())
                    .orElseThrow(() -> new RuntimeException("Tour not found")));
        }
        if (dto.getCustomerId() != null) {
            delivery.setCustomer(customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found")));
        }



        Delivery updated = deliveryRepository.save(delivery);
        return DeliveryMapper.toDTO(updated);
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        DeliveryStatus newStatus = DeliveryStatus.valueOf(status.toUpperCase());


        Delivery updatedDelivery = deliveryService.updateDeliveryStatus(id, newStatus);

        return ResponseEntity.ok(DeliveryMapper.toDTO(updatedDelivery));
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        deliveryRepository.deleteById(id);
    }
}
