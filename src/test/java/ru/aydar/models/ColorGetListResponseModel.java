package ru.aydar.models;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ColorGetListResponseModel extends BasicGetListResponseModel {
    ArrayList<ColorDataModel> data;
}
