package ru.aydar.tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тесты для API сайта Reqres.in")
public class ReqresTest {
    @Test
    @DisplayName("Проверка создания пользователя")
    void createUserTest()
    {
        String body = "{ \"name\": \"Vasya Pupkin\", \"job\": \"Jester\" }";
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .log().method()
                .log().uri()
                .log().body()
        .when()
                .post("https://reqres.in/api/users")
        .then()
                .log().status()
                .log().body()
                .statusCode(201);
    }

    @Test
    @DisplayName("Проверка редактирования пользователя")
    void editUserTest()
    {
        String body = "{ \"name\": \"Emma Wong\", \"job\": \"Associate Jester\" }";
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .log().method()
                .log().uri()
                .log().body()
        .when()
                .patch("https://reqres.in/api/users/3")
        .then()
                .log().status()
                .log().body()
                .statusCode(200);
    }

    @Test
    @DisplayName("Проверка регистрации пользователя")
    void registerUserTest()
    {
        String body = "{\"email\": \"eve.holt@reqres.in\", \"password\": \"TotallyNotAJester2007\"}";
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .log().method()
                .log().uri()
                .log().body()
        .when()
                .post("https://reqres.in/api/register")
        .then()
                .log().status()
                .log().body()
                .statusCode(200);
    }

    @Test
    @DisplayName("Проверка ответа на GET-запрос к /api/users на соответствие JSON-схеме")
    void usersSchemaTest()
    {
        given()
                .contentType(ContentType.JSON)
                .log().method()
                .log().uri()
        .when()
                .get("https://reqres.in/api/users")
        .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("users_schema.json"));
    }

    @Test
    @DisplayName("Проверка того, что у пользователя по имени Janet фамилия Weaver")
    void checkUsersLastNameTest()
    {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .log().method()
                        .log().uri()
                .when()
                        .get("https://reqres.in/api/users")
                .then()
                        .log().status()
                        .log().body()
                        .statusCode(200)
                        .extract().response();
        assertEquals("Weaver", response.path("data.find{it.first_name == 'Janet'}.last_name"));
    }

    @Test
    @DisplayName("Проверка того, что у фуксии номер Pantone равен 17-2031")
    void checkPantoneCodeTest()
    {
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .log().method()
                        .log().uri()
                .when()
                        .get("https://reqres.in/api/unknown")
                .then()
                        .log().status()
                        .log().body()
                        .statusCode(200)
                        .extract().response();
        assertEquals("17-2031", response.path("data.find{it.name == 'fuchsia rose'}.pantone_value"));
    }

    @Test
    @DisplayName("Проверка кода ответа 404 для несуществующего id пользователя")
    void checkUser404Test()
    {
        given()
                .contentType(ContentType.JSON)
                .log().method()
                .log().uri()
        .when()
                .get("https://reqres.in/api/users/13")
        .then()
                .log().status()
                .log().body()
                .statusCode(404);
    }

    @Test
    @DisplayName("Проверка кода ответа 404 для несуществующего id цвета")
    void checkUnknown404Test()
    {
        given()
                .contentType(ContentType.JSON)
                .log().method()
                .log().uri()
        .when()
                .get("https://reqres.in/api/unknown/13")
        .then()
                .log().status()
                .log().body()
                .statusCode(404);
    }
}
