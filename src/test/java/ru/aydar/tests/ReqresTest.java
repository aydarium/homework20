package ru.aydar.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        String body = "{ \"name\": \"Vasya Pupkin\", \"job\": \"Jester\" }";
        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .log().method()
                .log().uri()
                .log().body()
                .when()
                .post("/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .extract().response();
        assertAll("Проверка полей пользователя в ответе",
                () -> assertThat(response.path("name"), equalTo("Vasya Pupkin")),
                () -> assertThat(response.path("job"), equalTo("Jester")));
    }

    @Test
    @DisplayName("Проверка редактирования пользователя")
    void editUserTest() {
        String body = "{ \"name\": \"Emma Wong\", \"job\": \"Associate Jester\" }";
        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .log().method()
                .log().uri()
                .log().body()
                .when()
                .patch("/users/3")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().response();
        assertAll("Проверка полей пользователя в ответе",
                () -> assertThat(response.path("name"), equalTo("Emma Wong")),
                () -> assertThat(response.path("job"), equalTo("Associate Jester")));
    }

    @Test
    @DisplayName("Проверка регистрации пользователя")
    void registerUserTest() {
        String body = "{\"email\": \"eve.holt@reqres.in\", \"password\": \"TotallyNotAJester2007\"}";
        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .log().method()
                .log().uri()
                .log().body()
                .when()
                .post("/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().response();
        assertAll("Проверка данных регистрации в ответе",
                () -> assertThat(response.path("id"), equalTo(4)),
                () -> assertThat(response.path("token"), equalTo("QpwL5tke4Pnpja7X4")));
    }

    @Test
    @DisplayName("Проверка того, что у пользователя по имени Janet фамилия Weaver")
    void checkUsersLastNameTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .log().method()
                .log().uri()
                .when()
                .get("/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("users_schema.json"))
                .extract().response();
        assertEquals("Weaver", response.path("data.find{it.first_name == 'Janet'}.last_name"));
    }

    @Test
    @DisplayName("Проверка того, что у фуксии номер Pantone равен 17-2031")
    void checkPantoneCodeTest() {
        Response response = given()
                .contentType(ContentType.JSON)
                .log().method()
                .log().uri()
                .when()
                .get("/unknown")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().response();
        assertEquals("17-2031", response.path("data.find{it.name == 'fuchsia rose'}.pantone_value"));
    }

    @Test
    @DisplayName("Проверка кода ответа 404 для несуществующего id пользователя")
    void checkUser404Test() {
        given()
                .contentType(ContentType.JSON)
                .log().method()
                .log().uri()
                .when()
                .get("/users/13")
                .then()
                .log().status()
                .log().body()
                .statusCode(404);
    }

    @Test
    @DisplayName("Проверка кода ответа 404 для несуществующего id цвета")
    void checkUnknown404Test() {
        given()
                .contentType(ContentType.JSON)
                .log().method()
                .log().uri()
                .when()
                .get("/unknown/13")
                .then()
                .log().status()
                .log().body()
                .statusCode(404);
    }
}
