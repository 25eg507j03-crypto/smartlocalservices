
package com.smart.local.services.repoistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.local.services.entity.ServiceRequest;

public interface ServiceRequestRepository
        extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByUserId(Long userId);
}
