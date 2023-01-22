package com.pathfinder.test.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfinder.test.dto.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@SuppressWarnings("unused")
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private static final TypeReference<List<Country>> TYPE_REF = new TypeReference<>() {
    };

    @Value("${data.url}")
    private String dataUrl;

    private final ObjectMapper objectMapper;

    public List<Country> loadCountries() {
        try {
            return loadFromUrl();
        } catch (IOException e) {
            log.error("unable to load data file from URL: " + dataUrl);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unable to load data file from URL: " + dataUrl);
        }
    }

    private List<Country> loadFromUrl() throws IOException {
        return objectMapper.readValue(new URL(dataUrl), TYPE_REF);
    }

}
