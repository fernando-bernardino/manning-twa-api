package com.twa.flights.api.itineraries.search.facade;

import java.util.*;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.twa.flights.api.itineraries.search.connector.ProviderBetaConnector;
import com.twa.flights.common.dto.enums.Provider;
import com.twa.flights.common.dto.itinerary.ItineraryDTO;
import com.twa.flights.common.dto.request.AvailabilityRequestDTO;

@Component
public class ProviderBetaFacade implements ProviderFacade {

    static final Logger LOGGER = LoggerFactory.getLogger(ProviderBetaFacade.class);

    ProviderBetaConnector providerBetaConnector;

    @Autowired
    public ProviderBetaFacade(ProviderBetaConnector providerBetaConnector) {
        this.providerBetaConnector = providerBetaConnector;
    }

    @CircuitBreaker(name = "pricing", fallbackMethod = "empty")
    public List<ItineraryDTO> availability(AvailabilityRequestDTO request) {
        LOGGER.debug("Obtain the information about the flights");
        return providerBetaConnector.availability(request);
    }

    @Override
    public Provider getProvider() {
        return Provider.BETA;
    }

    private List<ItineraryDTO> empty(AvailabilityRequestDTO request, Throwable throwable) {
        LOGGER.error("Fallback for '/api/flights/provider/alpha/itineraries'");
        return Collections.emptyList();
    }
}
