package ru.aydar.tests;

import io.restassured.RestAssured;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.aydar.models.*;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.aydar.specs.ReqresSpec.*;
import static ru.aydar.utils.SearchByFieldUtil.getLastNameByFirstName;
import static ru.aydar.utils.SearchByFieldUtil.getPantoneCodeByColorName;

@DisplayName("Тесты для API сайта Reqres.in")
public class ReqresTest {
    @BeforeAll
    static void restAssuredBase() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.basePath = "/api";
    }

    @Test
    @DisplayName("Проверка создания пользователя")
    void createUserTest() {
        UserCreateUpdateRequestModel body = new UserCreateUpdateRequestModel();
        body.setName("Vasya Pupkin");
        body.setJob("Jester");
        UserCreateResponseModel response = step("Отправка запроса на создание пользователя", () ->
                given(basicRequestSpec)
                        .body(body)
                        .when()
                        .post("/users")
                        .then()
                        .spec(created201ResponseSpec)
                        .extract().as(UserCreateResponseModel.class));
        SoftAssertions userInfo = new SoftAssertions();
        userInfo.assertThat(response.getName()).as("Имя пользователя").isEqualTo("Vasya Pupkin");
        userInfo.assertThat(response.getJob()).as("Профессия пользователя").isEqualTo("Jester");
        step("Проверка полей созданного пользователя в ответе", () -> userInfo.assertAll());
    }

    @Test
    @DisplayName("Проверка редактирования пользователя")
    void editUserTest() {
        UserCreateUpdateRequestModel body = new UserCreateUpdateRequestModel();
        body.setName("Emma Wong");
        body.setJob("Associate Jester");
        UserUpdateResponseModel response = step("Отправка запроса на редактирование пользователя", () ->
                given(basicRequestSpec)
                        .body(body)
                        .when()
                        .patch("/users/3")
                        .then()
                        .spec(ok200ResponseSpec)
                        .extract().as(UserUpdateResponseModel.class));
        SoftAssertions userInfo = new SoftAssertions();
        userInfo.assertThat(response.getName()).as("Имя пользователя").isEqualTo("Emma Wong");
        userInfo.assertThat(response.getJob()).as("Профессия пользователя").isEqualTo("Associate Jester");
        step("Проверка отредактированых полей пользователя в ответе", () -> userInfo.assertAll());
    }

    @Test
    @DisplayName("Проверка регистрации пользователя")
    void registerUserTest() {
        UserRegisterRequestModel body = new UserRegisterRequestModel();
        body.setEmail("eve.holt@reqres.in");
        body.setPassword("TotallyNotAJester2007");
        UserRegisterResponseModel response = step("Отправка запроса на регистрацию пользователя", () ->
                given(basicRequestSpec)
                        .body(body)
                        .when()
                        .post("/register")
                        .then()
                        .spec(ok200ResponseSpec)
                        .extract().as(UserRegisterResponseModel.class));
        step("Проверка токена зарегистрированного пользователя в ответе", () ->
                assertThat(response.getToken().length()).as("Количество символов в токене %s", response.getToken()).isEqualTo(17));
    }

    @Test
    @DisplayName("Проверка того, что у пользователя по имени Janet фамилия Weaver")
    void checkUsersLastNameTest() {
        UserGetListResponseModel response = step("Отправка запроса на получение списка пользователей (первая страница)", () ->
                given(basicRequestSpec)
                        .when()
                        .get("/users")
                        .then()
                        .spec(ok200ResponseSpec)
                        .body(matchesJsonSchemaInClasspath("users_schema.json"))
                        .extract().as(UserGetListResponseModel.class));
        step("Проверка соответствия фамилии пользователя его имени в ответе", () ->
                assertThat(getLastNameByFirstName(response.getData(), "Janet")).isEqualTo("Weaver"));
    }

    @Test
    @DisplayName("Проверка того, что у фуксии номер Pantone равен 17-2031")
    void checkPantoneCodeTest() {
        ColorGetListResponseModel response = step("Отправка запроса на получение списка цветов (первая страница)", () ->
                given(basicRequestSpec)
                        .when()
                        .get("/unknown")
                        .then()
                        .spec(ok200ResponseSpec)
                        .extract().as(ColorGetListResponseModel.class));
        step("Проверка соответствия Pantone-кода цвета его названию в ответе", () ->
                assertThat(getPantoneCodeByColorName(response.getData(), "fuchsia rose")).as("Pantone-код").isEqualTo("17-2031"));
    }

    @Test
    @DisplayName("Проверка кода ответа 404 для несуществующего id пользователя")
    void checkUser404Test() {
        step("Отправка запроса на получение данных несуществующего пользователя", () ->
                given(basicRequestSpec)
                        .when()
                        .get("/users/13")
                        .then()
                        .spec(notFound404ResponseSpec));
    }

    @Test
    @DisplayName("Проверка кода ответа 404 для несуществующего id цвета")
    void checkUnknown404Test() {
        step("Отправка запроса на получение данных несуществующего цвета", () ->
                given(basicRequestSpec)
                        .when()
                        .get("/unknown/13")
                        .then()
                        .spec(notFound404ResponseSpec));
    }
}
