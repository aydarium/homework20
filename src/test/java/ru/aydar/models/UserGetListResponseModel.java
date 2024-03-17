package ru.aydar.models;

import lombok.Data;

import java.util.ArrayList;

@Data
public class UserGetListResponseModel extends BasicGetListResponseModel {
    ArrayList<UserDataModel> data;
}
