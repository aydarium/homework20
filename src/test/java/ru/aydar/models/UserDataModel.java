package ru.aydar.models;

import lombok.Data;

@Data
public class UserDataModel {
    int id;
    String email, first_name, last_name, avatar;
}
