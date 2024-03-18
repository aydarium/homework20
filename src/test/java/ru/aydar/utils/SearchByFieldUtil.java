package ru.aydar.utils;

import io.qameta.allure.Step;
import ru.aydar.models.ColorDataModel;
import ru.aydar.models.UserDataModel;

import java.util.ArrayList;

public class SearchByFieldUtil {
    @Step("Поиск пользователя по имени '{1}' и получение его фамилии")
    public static String getLastNameByFirstName(ArrayList<UserDataModel> users, String firstName) {
        for (UserDataModel user : users) {
            if (user.getFirstName().equals(firstName)) return user.getLastName();
        }
        return null;
    }

    @Step("Поиск цвета по названию '{1}' и получение его Pantone-кода")
    public static String getPantoneCodeByColorName(ArrayList<ColorDataModel> colors, String name) {
        for (ColorDataModel color : colors) {
            if (color.getName().equals(name)) return color.getPantoneValue();
        }
        return null;
    }
}
