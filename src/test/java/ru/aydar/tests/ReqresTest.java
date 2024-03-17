package ru.aydar.tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.aydar.models.*;

import java.util.ArrayList;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static ru.aydar.specs.ReqresSpec.*;

@DisplayName("Тесты для API сайта Reqres.in")
public class ReqresTest {
    @Step("Поиск пользователя по имени '{1}' и получение его фамилии")
    String getLastNameByFirstName(ArrayList<UserDataModel> users, String firstName) {
        for (UserDataModel user : users) {
            if (user.getFirst_name().equals(firstName)) return user.getLast_name();
        }
        return null;
    }

    @Step("Поиск цвета по названию '{1}' и получение его Pantone-кода")
    String getPantoneCodeByColorName(ArrayList<ColorDataModel> colors, String name) {
        for (ColorDataModel color : colors) {
            if (color.getName().equals(name)) return color.getPantone_value();
        }
        return null;
    }

    @BeforeAll
    static void restAssuredBase() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("Проверка создания пользователя")
    void createUserTest() {
        UserRequestBodyModel body = new UserRequestBodyModel();
        body.setName("Vasya Pupkin");
        body.setJob("Jester");
        UserCreateResponseModel response = step("Отправка запроса на создание пользователя", () ->
                given(basicRequestSpec)
                        .body(body)
                        .when()
                        .post("/users")
                        .then()
                        .spec(createdResponseSpec)
                        .extract().as(UserCreateResponseModel.class));
        step("Проверка полей созданного пользователя в ответе", () ->
                assertAll(
                        () -> assertThat(response.getName(), equalTo("Vasya Pupkin")),
                        () -> assertThat(response.getJob(), equalTo("Jester"))));
    }

    @Test
    @DisplayName("Проверка редактирования пользователя")
    void editUserTest() {
        UserRequestBodyModel body = new UserRequestBodyModel();
        body.setName("Emma Wong");
        body.setJob("Associate Jester");
        UserUpdateResponseModel response = step("Отправка запроса на редактирование пользователя", () ->
                given(basicRequestSpec)
                        .body(body)
                        .when()
                        .patch("/users/3")
                        .then()
                        .spec(okResponseSpec)
                        .extract().as(UserUpdateResponseModel.class));
        step("Проверка отредактированых полей пользователя в ответе", () ->
                assertAll(
                        () -> assertThat(response.getName(), equalTo("Emma Wong")),
                        () -> assertThat(response.getJob(), equalTo("Associate Jester"))));
    }

    @Test
    @DisplayName("Проверка регистрации пользователя")
    void registerUserTest() {
        String body = "{\"email\": \"eve.holt@reqres.in\", \"password\": \"TotallyNotAJester2007\"}";
        UserRegisterResponseModel response = step("Отправка запроса на регистрацию пользователя", () ->
                given(basicRequestSpec)
                        .body(body)
                        .when()
                        .post("/register")
                        .then()
                        .spec(okResponseSpec)
                        .extract().as(UserRegisterResponseModel.class));
        step("Проверка полей зарегистрированного пользователя в ответе", () ->
                assertAll(
                        () -> assertThat(response.getId(), equalTo(4)),
                        () -> assertThat(response.getToken(), equalTo("QpwL5tke4Pnpja7X4"))));
    }

    @Test
    @DisplayName("Проверка того, что у пользователя по имени Janet фамилия Weaver")
    void checkUsersLastNameTest() {
        UserGetListResponseModel response = step("Отправка запроса на получение списка пользователей (первая страница)", () ->
                given(basicRequestSpec)
                        .when()
                        .get("/users")
                        .then()
                        .spec(okResponseSpec)
                        .body(matchesJsonSchemaInClasspath("users_schema.json"))
                        .extract().as(UserGetListResponseModel.class));
        step("Проверка соответствия фамилии пользователя его имени в ответе", () ->
                assertEquals("Weaver", getLastNameByFirstName(response.getData(), "Janet")));
    }

    @Test
    @DisplayName("Проверка того, что у фуксии номер Pantone равен 17-2031")
    void checkPantoneCodeTest() {
        ColorGetListResponseModel response = step("Отправка запроса на получение списка цветов (первая страница)", () ->
                given(basicRequestSpec)
                        .when()
                        .get("/unknown")
                        .then()
                        .spec(okResponseSpec)
                        .extract().as(ColorGetListResponseModel.class));
        step("Проверка соответствия Pantone-кода цвета его названию в ответе", () ->
                assertEquals("17-2031", getPantoneCodeByColorName(response.getData(),"fuchsia rose")));
    }

    @Test
    @DisplayName("Проверка кода ответа 404 для несуществующего id пользователя")
    void checkUser404Test() {
        step("Отправка запроса на получение данных несуществующего пользователя", () ->
                given(basicRequestSpec)
                        .when()
                        .get("/users/13")
                        .then()
                        .spec(notFoundResponseSpec));
    }

    @Test
    @DisplayName("Проверка кода ответа 404 для несуществующего id цвета")
    void checkUnknown404Test() {
        step("Отправка запроса на получение данных несуществующего цвета", () ->
                given(basicRequestSpec)
                        .when()
                        .get("/unknown/13")
                        .then()
                        .spec(notFoundResponseSpec));
    }
}
