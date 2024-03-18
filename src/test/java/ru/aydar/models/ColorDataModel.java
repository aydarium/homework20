package ru.aydar.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ColorDataModel {
    int id;
    String name, year, color;

    @JsonProperty("pantone_value")
    String pantoneValue;
}
