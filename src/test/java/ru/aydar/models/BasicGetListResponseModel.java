package ru.aydar.models;

import lombok.Data;

@Data
public class BasicGetListResponseModel {
    int page, per_page, total, total_pages;
    UserListSupportModel support;
}
