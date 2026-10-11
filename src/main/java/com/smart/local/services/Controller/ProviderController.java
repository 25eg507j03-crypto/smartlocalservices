
package com.smart.local.services.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smart.local.services.entity.Provider;
import com.smart.local.services.service.ProviderService;

@RestController
@RequestMapping("/providers")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    // Get all providers, or search using a query parameter
    @GetMapping
    public List<Provider> getProviders(
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String businessName) {

        if (serviceType != null && !serviceType.isBlank()) {
            return providerService.searchByServiceType(serviceType);
        }

        if (address != null && !address.isBlank()) {
            return providerService.searchByAddress(address);
        }

        if (businessName != null && !businessName.isBlank()) {
            return providerService.searchByBusinessName(businessName);
        }

        return providerService.getAllProviders();
    }

    // Get one provider by ID
    @GetMapping("/{id}")
    public ResponseEntity<Provider> getProviderById(
            @PathVariable Long id) {

        Optional<Provider> provider =
                providerService.getProviderById(id);

        if (provider.isPresent()) {
            return ResponseEntity.ok(provider.get());
        }

        return ResponseEntity.notFound().build();
    }
}
