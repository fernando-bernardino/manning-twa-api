package com.twa.flights.api.provider.alpha.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import com.twa.flights.api.provider.alpha.connector.CatalogConnector;
import com.twa.flights.api.provider.alpha.dto.CityDTO;

import static com.twa.flights.api.provider.alpha.configuration.CacheManagerConfiguration.CATALOG_CITY;

@Service
@CacheConfig
public class CatalogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogService.class);

    private CatalogConnector catalogConnector;

    @Autowired
    public CatalogService(CatalogConnector catalogConnector) {
        this.catalogConnector = catalogConnector;
    }

    public CityDTO getCity(String code) {
        LOGGER.debug("Obtain the information for code: {}", code);
        return catalogConnector.getCityByCode(code);
    }

}
