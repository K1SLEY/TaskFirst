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

    @Test
    public void getTopStudentWhenNoMarksReturnsEmpty() {
        String body1 = """ 
                      {
                      "id": 4,
                      "name" : "Anton",
                      "marks" : []
                      }
                """;
        String body2 = """ 
                      {
                      "id": 5,
                      "name" : "Ivan",
                      "marks" : null
                      }
                """;
        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body1).
                post("/student")
                .then()
                .statusCode(201);

        RestAssured.given().
                contentType(ContentType.JSON).
                body(body2).
                post("/student")
                .then()
                .statusCode(201);

        RestAssured.given()
                .when()
                .get("/topStudent")
                .then()
                .statusCode(200)
                .body(Matchers.emptyString());


        RestAssured.given()
                .when()
                .delete("/student/4")
                .then()
                .statusCode(200);

        RestAssured.given()
                .when()
                .delete("/student/5")
                .then()
                .statusCode(200);
    }

    @Test
    public void getTopStudentSingleTop() {
        String body1 = """ 
                      {
                      "id": 6,
                      "name" : "goodBoy",
                      "marks" : [5,5,5]
                      }
                """;
        String body2 = """ 
                      {
                      "id": 7,
                      "name" : "stupidBoy",
                      "marks" : [3,3]
                      }
                """;
        RestAssured.given().contentType(ContentType.JSON).body(body1).post("/student");
        RestAssured.given().contentType(ContentType.JSON).body(body2).post("/student");

        RestAssured.given()
                .when()
                .get("/topStudent")
                .then()
                .statusCode(200)
                .body("[0].id", Matchers.equalTo(6))
                .body("[0].name", Matchers.equalTo("goodBoy"))
                .body("[0].marks", Matchers.hasItems(5, 5, 5));

        RestAssured.given()
                .when()
                .delete("/student/6")
                .then()
                .statusCode(200);

        RestAssured.given()
                .when()
                .delete("/student/7")
                .then()
                .statusCode(200);
    }


    @Test
    public void getTopStudentMultipleTop() {
        String body1 = """ 
                      {
                      "id": 8,
                      "name" : "Anton",
                      "marks" : [5,5,5]
                      }
                """;
        String body2 = """ 
                      {
                      "id": 9,
                      "name" : "Ivan",
                      "marks" : [5,5,5]
                      }
                """;
        RestAssured.given().contentType(ContentType.JSON).body(body1).post("/student");
        RestAssured.given().contentType(ContentType.JSON).body(body2).post("/student");

        RestAssured.given()
                .when()
                .get("/topStudent")
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(2))
                .body("id", Matchers.containsInAnyOrder(8, 9))
                .body("name", Matchers.containsInAnyOrder("Anton", "Ivan"));

        RestAssured.given()
                .when()
                .delete("/student/8")
                .then()
                .statusCode(200);

        RestAssured.given()
                .when()
                .delete("/student/9")
                .then()
                .statusCode(200);
    }

}

