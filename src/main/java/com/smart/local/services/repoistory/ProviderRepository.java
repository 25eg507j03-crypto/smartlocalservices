
package com.smart.local.services.repoistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.local.services.entity.Provider;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    List<Provider> findByServiceTypeContainingIgnoreCase(String serviceType);

    List<Provider> findByAddressContainingIgnoreCase(String address);

    List<Provider> findByBusinessNameContainingIgnoreCase(String businessName);
}
