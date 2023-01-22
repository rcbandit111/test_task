package com.pathfinder.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {

    private String cca3;

    @EqualsAndHashCode.Exclude
    private List<String> borders = new ArrayList<>();

}
