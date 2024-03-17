package ru.aydar.models;

import lombok.Data;

@Data
public class UserRegisterResponseModel {
    int id;
    String token, error;
}
