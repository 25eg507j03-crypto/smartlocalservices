package com.smart.local.services.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.smart.local.services.entity.ServiceRequest;
import com.smart.local.services.repoistory.ServiceRequestRepository;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository repository;

    public ServiceRequestService(ServiceRequestRepository repository) {
        this.repository = repository;
    }

    public ServiceRequest saveServiceRequest(ServiceRequest request) {
        return repository.save(request);
    }

    public List<ServiceRequest> getAllServiceRequests() {
        return repository.findAll();
    }

    public Optional<ServiceRequest> getServiceRequestById(Long id) {
        return repository.findById(id);
    }

    public Optional<ServiceRequest> updateServiceRequest(
            Long id, ServiceRequest request) {

        return repository.findById(id).map(existing -> {
            existing.setUserId(request.getUserId());
            existing.setProviderId(request.getProviderId());
            existing.setServiceType(request.getServiceType());
            existing.setRequestDate(request.getRequestDate());
            existing.setStatus(request.getStatus());
            existing.setDescription(request.getDescription());
            existing.setAddress(request.getAddress());

            return repository.save(existing);
        });
    }

    public boolean deleteServiceRequest(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}