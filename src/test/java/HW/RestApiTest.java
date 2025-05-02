package HW;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class RestApiTest {

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;

    }

    @Test
    public void postStudent() {
        String body = """ 
                      {
                      "id": 1,
                      "name" : "Anton",
                      "marks" : [5,4,4]
                      }
                """;
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/student")
                .then()
                .statusCode(201);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/student/1")
                .then()
                .statusCode(200);
    }

    @Test
    public void getStudent() {

        String body = """ 
                      {
                      "id": 1,
                      "name" : "Anton",
                      "marks" : [5,4,4]
                      }
                """;
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/student")
                .then()
                .statusCode(201);


        RestAssured.given()
                .when()
                .get("/student/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Matchers.equalTo(1))
                .body("name", Matchers.equalTo("Anton"))
                .body("marks", Matchers.contains(5, 4, 4));


        RestAssured.given()
                .when()
                .delete("/student/1")
                .then()
                .statusCode(200);
    }


    @Test
    public void getStudentNotFound() {
        RestAssured.given()
                .when()
                .get("/student/-1")
                .then()
                .statusCode(404);

    }

    @Test
    public void updateStudentAndGetUpdatedStudent() {
        String body1 = """ 
                      {
                      "id": 2,
                      "name" : "Anton",
                      "marks" : [2,2,2]
                      }
                """;
        String body2 = """ 
                      {
                      "id": 2,
                      "name" : "Ivan",
                      "marks" : [5,5,5]
                      }
                """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body1)
                .when()
                .post("/student")
                .then()
                .statusCode(201);

        RestAssured.given()
                .when()
                .get("/student/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Matchers.equalTo(2))
                .body("name", Matchers.equalTo("Anton"))
                .body("marks", Matchers.hasItems(2, 2, 2));


        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body2)
                .when()
                .post("/student")
                .then()
                .statusCode(201);

        RestAssured.given()
                .when()
                .get("/student/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", Matchers.equalTo(2))
                .body("name", Matchers.equalTo("Ivan"))
                .body("marks", Matchers.hasItems(5, 5, 5));


        RestAssured.given()
                .when()
                .delete("/student/2")
                .then()
                .statusCode(200);

    }

    @Test
    public void postStudentIdNull() {
        String body = """ 
                      {
                      "id": null,
                      "name" : "Ivan",
                      "marks" : [2,2,2]
                      }
                """;
        Integer id = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/student")
                .then()
                .statusCode(201)
                .extract()
                .path("$");

        Assertions.assertNotNull(id);

        RestAssured.given()
                .when()
                .get("/student/" + id)
                .then()
                .statusCode(200)
                .body("name", Matchers.equalTo("Ivan"));

        RestAssured.given()
                .when()
                .delete("/student/" + id)
                .then()
                .statusCode(200);
    }

    @Test
    public void postStudentWithoutNameReturns400() {
        String body = """ 
                      {
                      "id": 10,
                      "marks" : [2,2,2]
                      }
                """;
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/student")
                .then()
                .statusCode(400);
    }

    @Test
    public void postStudentWithNullNameReturns400() {
        String body = """ 
                      {
                      "id": 3,
                      "name" : null,
                      "marks" : [5,4,4]
                      }
                """;
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/student")
                .then()
                .statusCode(400);
    }

    @Test
    public void deleteStudentReturns200() {
        String body = """ 
                      {
                      "id": 3,
                      "name" : "Anton",
                      "marks" : []
                      }
                """;
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/student");

        RestAssured.given()
                .when()
                .delete("/student/3")
                .then()
                .statusCode(200);

        RestAssured.given()
                .when()
                .get("/student/3")
                .then()
                .statusCode(404);
    }

    @Test
    public void deleteStudentNotFoundReturns404() {
        RestAssured.given()
                .when()
                .delete("/student/-1")
                .then()
                .statusCode(404);
    }

    @Test
    public void getTopStudentWithoutStudent() {
        RestAssured.given()
                .when()
                .get("/topStudent/")
                .then()
                .statusCode(200)
                .body(Matchers.emptyString());
    }


}


/*
get /topStudent код 200 и пустое тело, если студентов в базе нет.
get /topStudent код 200 и пустое тело, если ни у кого из студентов в базе нет оценок.
get /topStudent код 200 и один студент, если у него максимальная средняя оценка, либо же среди всех студентов с максимальной средней у него их больше всего.
get /topStudent код 200 и несколько студентов, если у них всех эта оценка максимальная и при этом они равны по количеству оценок.
 */