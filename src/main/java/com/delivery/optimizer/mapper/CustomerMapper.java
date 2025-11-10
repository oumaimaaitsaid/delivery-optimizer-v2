package com.delivery.optimizer.mapper;

import com.delivery.optimizer.dto.CustomerDTO;
import com.delivery.optimizer.model.Customer;

// Hada ghir class 3adia, ma fiha la @Component la walo
public class CustomerMapper {

    /**
     * entité to DTO
     */
    public static CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setAddress(customer.getAddress());
        dto.setLatitude(customer.getLatitude());
        dto.setLongitude(customer.getLongitude());
        dto.setPreferredTimeSlot(customer.getPreferredTimeSlot());

        return dto;
    }

    /**
     *  DTO to entité
     */
    public static Customer toEntity(CustomerDTO dto) {
        if (dto == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setId(dto.getId());
        customer.setName(dto.getName());
        customer.setAddress(dto.getAddress());
        customer.setLatitude(dto.getLatitude());
        customer.setLongitude(dto.getLongitude());
        customer.setPreferredTimeSlot(dto.getPreferredTimeSlot());

        return customer;
    }
}