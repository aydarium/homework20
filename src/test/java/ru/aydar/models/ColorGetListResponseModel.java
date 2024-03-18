package ru.aydar.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ColorGetListResponseModel {
    ArrayList<ColorDataModel> data;
}
