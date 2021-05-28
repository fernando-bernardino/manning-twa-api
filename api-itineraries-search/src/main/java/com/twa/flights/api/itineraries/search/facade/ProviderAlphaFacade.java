package com.twa.flights.api.itineraries.search.facade;

import java.util.*;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.twa.flights.api.itineraries.search.connector.ProviderAlphaConnector;
import com.twa.flights.common.dto.enums.Provider;
import com.twa.flights.common.dto.itinerary.ItineraryDTO;
import com.twa.flights.common.dto.request.AvailabilityRequestDTO;

@Component
public class ProviderAlphaFacade implements ProviderFacade {

    static final Logger LOGGER = LoggerFactory.getLogger(ProviderAlphaFacade.class);

    ProviderAlphaConnector itinerariesSearchConnector;

    @Autowired
    public ProviderAlphaFacade(ProviderAlphaConnector itinerariesSearchConnector) {
        this.itinerariesSearchConnector = itinerariesSearchConnector;
    }

    @RateLimiter(name = "consumer", fallbackMethod = "empty")
    @CircuitBreaker(name = "provider-alpha")
    public List<ItineraryDTO> availability(AvailabilityRequestDTO request) {
        LOGGER.debug("Obtain the information about the flights");
        return itinerariesSearchConnector.availability(request);
    }

    private List<ItineraryDTO> empty(AvailabilityRequestDTO request, Throwable throwable) {
        LOGGER.error("Falling back to empty list", throwable);
        return Collections.emptyList();
    }

    @Override
    public Provider getProvider() {
        return Provider.ALPHA;
    }

}
