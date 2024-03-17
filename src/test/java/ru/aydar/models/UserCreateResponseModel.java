package ru.aydar.models;

import lombok.Data;

@Data
public class UserCreateResponseModel {
    String name, job, id, createdAt;
}
