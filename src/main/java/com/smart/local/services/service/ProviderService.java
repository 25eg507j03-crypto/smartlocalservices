
package com.smart.local.services.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.smart.local.services.entity.Provider;
import com.smart.local.services.repoistory.ProviderRepository;

@Service
public class ProviderService {

    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    // Get all providers
    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    // Get a provider by ID
    public Optional<Provider> getProviderById(Long id) {
        return providerRepository.findById(id);
    }

    // Search providers by service type
    public List<Provider> searchByServiceType(String serviceType) {
        return providerRepository
                .findByServiceTypeContainingIgnoreCase(serviceType);
    }

    // Search providers by address
    public List<Provider> searchByAddress(String address) {
        return providerRepository
                .findByAddressContainingIgnoreCase(address);
    }

    // Search providers by business name
    public List<Provider> searchByBusinessName(String businessName) {
        return providerRepository
                .findByBusinessNameContainingIgnoreCase(businessName);
    }

    // Add or update a provider
    public Provider saveProvider(Provider provider) {
        return providerRepository.save(provider);
    }

    // Delete a provider
    public void deleteProvider(Long id) {
        providerRepository.deleteById(id);
    }
}
