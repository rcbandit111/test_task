package com.pathfinder.test.service;

import com.pathfinder.test.dto.Country;
import com.pathfinder.test.dto.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PathfinderService {

    private final DataLoader dataLoader;

    private Map<String, Country> countryMap;
    private DijkstraShortestPath<Country, DefaultEdge> dijkstraShortestPath;

    public Route findRoute(final String origin, final String destination) {
        log.info("origin = {}, destination = {}", origin, destination);
        final Country originCountry = Optional.ofNullable(countryMap.get(origin.toUpperCase()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Origin country " + origin + " not found"));
        final Country destinationCountry = Optional.ofNullable(countryMap.get(destination.toUpperCase()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destination country " + destination + " not found"));

        final GraphPath<Country, DefaultEdge> path = dijkstraShortestPath.getPath(originCountry, destinationCountry);
        if (path == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No routes found");
        }

        final List<String> routeCodes = path.getVertexList().stream()
                .map(Country::getCca3)
                .collect(Collectors.toList());
        log.info("possible route is: {}", routeCodes);
        return new Route(routeCodes);
    }

    @PostConstruct
    protected void init() {

        countryMap = dataLoader.loadCountries().stream()
                .collect(Collectors.toMap(Country::getCca3, c -> c));

        final Graph<Country, DefaultEdge> graph = new DefaultUndirectedGraph<>(DefaultEdge.class);

        try {
            countryMap.values().forEach(graph::addVertex);
            countryMap.values().stream()
                    .map(this::getEdges)
                    .flatMap(Collection::stream)
                    .forEach(edge -> graph.addEdge(edge.getFirst(), edge.getSecond()));
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "unable to construct a graph from countries", e);
        }

        dijkstraShortestPath = new DijkstraShortestPath<>(graph);
    }


    private List<Pair<Country, Country>> getEdges(final Country country) {
        return country.getBorders().stream()
                .map(countryMap::get)
                .map(neighbour -> new Pair<>(country, neighbour))
                .collect(Collectors.toList());
    }
}
