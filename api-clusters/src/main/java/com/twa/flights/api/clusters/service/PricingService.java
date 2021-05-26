package com.twa.flights.api.clusters.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.twa.flights.api.clusters.dto.*;
import com.twa.flights.common.dto.itinerary.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.twa.flights.api.clusters.connector.PricingConnector;

@Service
public class PricingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PricingService.class);

    private PricingConnector pricingConnector;

    @Autowired
    public PricingService(PricingConnector pricingConnector) {
        this.pricingConnector = pricingConnector;
    }

    @CircuitBreaker(name = "pricing", fallbackMethod = "fallbackPriceItineraries")
    public List<ItineraryDTO> priceItineraries(List<ItineraryDTO> itineraries) {
        LOGGER.debug("Pricing itineraries");

        List<UpdatedPriceInfoDTO> updatedPrices = pricingConnector.priceItineraries(itineraries);

        for (UpdatedPriceInfoDTO updatedPriceInfo : updatedPrices) {
            Optional<ItineraryDTO> itinerary = itineraries.stream()
                    .filter(iti -> iti.getId().equals(updatedPriceInfo.getItineraryId())).findFirst();

            if (itinerary.isPresent()) {
                updatePriceInfo(updatedPriceInfo, itinerary.get());
            }
        }

        return itineraries.stream().filter(iti -> iti.getPriceInfo().getAdults().getMarkup() != null)
                .collect(Collectors.toList());
    }

    private void updatePriceInfo(UpdatedPriceInfoDTO updatedPriceInfo, ItineraryDTO itinerary) {
        PriceInfoDTO priceInfo = itinerary.getPriceInfo();
        priceInfo.getAdults().setMarkup(updatedPriceInfo.getAdults().getMarkup());
        priceInfo.getAdults().setTotal(updatedPriceInfo.getAdults().getTotal());

        if (priceInfo.getChildren() != null) {
            priceInfo.getChildren().setMarkup(updatedPriceInfo.getChildren().getMarkup());
            priceInfo.getChildren().setTotal(updatedPriceInfo.getChildren().getTotal());
        }

        if (priceInfo.getInfants() != null) {
            priceInfo.getInfants().setMarkup(updatedPriceInfo.getInfants().getMarkup());
            priceInfo.getInfants().setTotal(updatedPriceInfo.getInfants().getTotal());
        }
    }


    private List<ItineraryDTO> fallbackPriceItineraries(List<ItineraryDTO> itineraries, Throwable throwable) {
        LOGGER.error("Fallback for '/api/flights/pricing/itineraries'", throwable);
        itineraries.forEach(i -> updatePriceInfo(zeroUpdatedPriceInfoDTO(), i));
        return itineraries;
    }

    private UpdatedPriceInfoDTO zeroUpdatedPriceInfoDTO() {
        UpdatedPriceInfoDTO dto = new UpdatedPriceInfoDTO();
        dto.setAdults(zeroPriceInfoDTO());
        dto.setInfants(zeroPriceInfoDTO());
        dto.setChildren(zeroPriceInfoDTO());
        return dto;
    }

    private UpdatedPaxPriceDTO zeroPriceInfoDTO() {
        UpdatedPaxPriceDTO zeroMarkup = new UpdatedPaxPriceDTO();
        zeroMarkup.setMarkup(new MarkupDTO(BigDecimal.ZERO, BigDecimal.ZERO));
        return zeroMarkup;
    }
}
