
package com.smart.local.services.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.local.services.entity.ServiceRequest;
import com.smart.local.services.repoistory.ServiceRequestRepository;

@RestController
@RequestMapping("/service-requests")
public class ServiceRequestController {

    private final ServiceRequestRepository serviceRequestRepository;

    public ServiceRequestController(
            ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
    }

    private Long getLoggedInUserId(HttpSession session) {
        Object userId = session.getAttribute("userId");

        if (userId instanceof Number) {
            return ((Number) userId).longValue();
        }

        return null;
    }

    private ResponseEntity<?> loginRequired() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Please log in."));
    }

    @PostMapping
    public ResponseEntity<?> createServiceRequest(
            @RequestBody ServiceRequest serviceRequest,
            HttpSession session) {

        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return loginRequired();
        }

        // Always use the logged-in user's ID.
        serviceRequest.setUserId(userId);

        if (serviceRequest.getStatus() == null
                || serviceRequest.getStatus().isBlank()) {
            serviceRequest.setStatus("Pending");
        }

        ServiceRequest savedRequest =
                serviceRequestRepository.save(serviceRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
    }

    @GetMapping
    public ResponseEntity<?> getAllServiceRequests(HttpSession session) {
        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return loginRequired();
        }

        List<ServiceRequest> requests =
                serviceRequestRepository.findByUserId(userId);

        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceRequestById(
            @PathVariable Long id, HttpSession session) {

        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return loginRequired();
        }

        Optional<ServiceRequest> request =
                serviceRequestRepository.findById(id);

        if (request.isEmpty()
                || !userId.equals(request.get().getUserId())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(request.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateServiceRequest(
            @PathVariable Long id,
            @RequestBody ServiceRequest updatedRequest,
            HttpSession session) {

        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return loginRequired();
        }

        Optional<ServiceRequest> existing =
                serviceRequestRepository.findById(id);

        if (existing.isEmpty()
                || !userId.equals(existing.get().getUserId())) {
            return ResponseEntity.notFound().build();
        }

        ServiceRequest request = existing.get();

        // Update booking details without changing its owner,
        // provider, or status through this endpoint.
        request.setServiceType(updatedRequest.getServiceType());
        request.setRequestDate(updatedRequest.getRequestDate());
        request.setDescription(updatedRequest.getDescription());
        request.setAddress(updatedRequest.getAddress());

        ServiceRequest savedRequest =
                serviceRequestRepository.save(request);

        return ResponseEntity.ok(savedRequest);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdate statusUpdate,
            HttpSession session) {

        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return loginRequired();
        }

        Optional<ServiceRequest> existing =
                serviceRequestRepository.findById(id);

        if (existing.isEmpty()
                || !userId.equals(existing.get().getUserId())) {
            return ResponseEntity.notFound().build();
        }

        if (statusUpdate.getStatus() == null
                || statusUpdate.getStatus().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Status is required."));
        }

        ServiceRequest request = existing.get();
        request.setStatus(statusUpdate.getStatus());

        ServiceRequest savedRequest =
                serviceRequestRepository.save(request);

        return ResponseEntity.ok(savedRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServiceRequest(
            @PathVariable Long id, HttpSession session) {

        Long userId = getLoggedInUserId(session);

        if (userId == null) {
            return loginRequired();
        }

        Optional<ServiceRequest> existing =
                serviceRequestRepository.findById(id);

        if (existing.isEmpty()
                || !userId.equals(existing.get().getUserId())) {
            return ResponseEntity.notFound().build();
        }

        serviceRequestRepository.delete(existing.get());

        return ResponseEntity.noContent().build();
    }

    public static class StatusUpdate {
        private String status;

        public StatusUpdate() {
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
