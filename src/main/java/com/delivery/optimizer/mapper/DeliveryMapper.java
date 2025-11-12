package com.delivery.optimizer.mapper;

import com.delivery.optimizer.dto.DeliveryDTO;
import com.delivery.optimizer.model.Delivery;

public class DeliveryMapper {

    public static DeliveryDTO toDTO(Delivery delivery) {
        DeliveryDTO dto = new DeliveryDTO();
        dto.setId(delivery.getId());
        dto.setLatitude(delivery.getLatitude());
        dto.setLongitude(delivery.getLongitude());
        dto.setWeight(delivery.getWeight());
        dto.setVolume(delivery.getVolume());
        dto.setTimeWindow(delivery.getTimeWindow());
        dto.setStatus(delivery.getStatus());
        dto.setAddress(delivery.getAddress());
        dto.setPreferredTimeSlot(delivery.getPreferredTimeSlot());
        dto.setPlannedTime(delivery.getPlannedTime());
        dto.setDate(delivery.getDate());

        // Kanakhdo l ID dial l tour o l customer
        if (delivery.getTour() != null) {
            dto.setTourId(delivery.getTour().getId());
        }
        if (delivery.getCustomer() != null) {
            dto.setCustomerId(delivery.getCustomer().getId());
        }
        return dto;

    }
    public static Delivery toEntity(DeliveryDTO dto) {
        Delivery delivery = new Delivery();
        delivery.setId(dto.getId());
        delivery.setLatitude(dto.getLatitude());
        delivery.setLongitude(dto.getLongitude());
        delivery.setWeight(dto.getWeight());
        delivery.setVolume(dto.getVolume());
        delivery.setTimeWindow(dto.getTimeWindow());
        delivery.setStatus(dto.getStatus());
            delivery.setAddress(dto.getAddress());
            delivery.setPreferredTimeSlot(dto.getPreferredTimeSlot());
            delivery.setPlannedTime(dto.getPlannedTime());
            delivery.setDate(dto.getDate());
        return delivery;
    }

}
