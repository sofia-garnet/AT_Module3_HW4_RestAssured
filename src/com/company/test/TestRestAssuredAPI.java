package com.company.test;

import java.util.*;

import com.company.main.data.CreateUserResponse;
import com.company.main.data.DeletePetResponse;
import com.company.main.data.DeleteUserResponse;
import com.company.main.data.Statuses;
import com.company.main.entities.User;
import com.company.main.entities.invalidPet.InvalidPet;
import com.company.main.entities.pet.Category;
import com.company.main.entities.pet.Tag;
import com.company.main.entities.pet.Pet;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static java.util.Arrays.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestRestAssuredAPI {
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void task1_createInvalidPet() {
        Category cats = new Category(1, "cats");
        Tag mini = new Tag(4, "mini");
        Tag big = new Tag(5, "big");
        Tag cat = new Tag(1, "cat");
        InvalidPet invalidPet1 = new InvalidPet(
                99999999999999999999999999999999d,
                cats,
                "Meow" + RandomStringUtils.randomAlphabetic(5),
                Collections.singletonList("urls"),
                asList(cat, mini),
                Statuses.sold.name());

        given()
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(invalidPet1)
                .post()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void task2_petPutResponse() {
        //        Preparing test data
        Category cats = new Category(1, "cats");
        Tag mini = new Tag(4, "mini");
        Tag big = new Tag(5, "big");
        Tag cat = new Tag(1, "cat");

        //создаем первого животного
        Pet newPet1 = new Pet(
                1 + (int) (Math.random() * 9),
                cats,
                "Meow" + RandomStringUtils.randomAlphabetic(5),
                Collections.singletonList("urls"),
                asList(cat, mini),
                Statuses.sold.name());

        Response responseAddPet1 = given()
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(newPet1)
                .post();

        assertEquals(200, responseAddPet1.getStatusCode());

        System.out.println("Response for adding a new pet1: \n" + responseAddPet1.asString() + "\n");
        Pet newAddedPet1 = responseAddPet1.as(Pet.class);
        newAddedPet1.setName("murka");
        newAddedPet1.setStatus(Statuses.available.name());

        given()
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(newAddedPet1)
                .when()
                .put()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        Response getPutPetResponse = given()
                .basePath("/pet/" + newAddedPet1.getId())
                .accept("application/json")
                .get().then().assertThat()
                .body("name", equalTo("murka"))
                .and()
                .body("status", equalTo(Statuses.available.name()))
                .extract()
                .response();

        System.out.println("Response for adding a new pet1: \n" + getPutPetResponse.asString() + "\n");

        // удалить своего питомца в конце теста, чтобы не засорять базу
        Response deleteResponse =
                given()
                        .pathParam("Id", newAddedPet1.getId())
                        .basePath("/pet/{Id}")
                        .accept("application/json")
                        .when()
                        .delete();
        System.out.println(deleteResponse.asString());

        DeletePetResponse deleteResponseAsClass = deleteResponse.as(DeletePetResponse.class);
        assertEquals(200, deleteResponseAsClass.getCode());
        assertNotNull(deleteResponseAsClass.getType());
        assertEquals(newAddedPet1.getId(), Long.parseLong(deleteResponseAsClass.getMessage()));
    }


    @Test
    public void task3_userJsonValidateSchema() {
        User newUser = new User(
                100000 + (long) (Math.random() * 999999),
                "user" + RandomStringUtils.randomAlphabetic(5),
                "firstName" + RandomStringUtils.randomAlphabetic(5),
                "lastName" + RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5) + "@test.com",
                RandomStringUtils.random(10, 0, 123, true, true),
                "+38097" + RandomStringUtils.randomNumeric(7),
                1000 + (int) (Math.random() * 999));

        //        Tests
        Response responseCreateUser = given()
                .basePath("/user")
                .contentType(ContentType.JSON)
                .body(newUser)
                .post();

        assertEquals(200, responseCreateUser.getStatusCode());
        System.out.println("Response for adding a new user: \n" + responseCreateUser.asString() + "\n"); // log info
        CreateUserResponse createdUser = responseCreateUser.as(CreateUserResponse.class);


        given()
                .pathParam("userName", newUser.getUsername())
                .basePath("/user/{userName}")
                .accept("application/json")
                .get()
                .then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("com/company/main/data/schema.json"));
        //.as(User.class);

        // удалить своего юзера в конце теста, чтобы не засорять базу
        Response deleteResponse =
                given()
                        .pathParam("userName", newUser.getUsername())
                        .basePath("/user/{userName}")
                        .accept("application/json")
                        .when()
                        .delete();
        System.out.println(deleteResponse.asString());
        DeleteUserResponse deleteResponseAsClass = deleteResponse.as(DeleteUserResponse.class);

        assertEquals(200, deleteResponseAsClass.getCode());
        assertNotNull(deleteResponseAsClass.getType());
        System.out.println(deleteResponseAsClass.getType());

    }

    @Test
    public void task4_deletePetById() {
        //        Preparing test data
        Category cats = new Category(1, "cats");
        Tag mini = new Tag(4, "mini");
        Tag big = new Tag(5, "big");
        Tag cat = new Tag(1, "cat");

        //создаем первого животного
        Pet newPet1 = new Pet(
                1 + (int) (Math.random() * 9),
                cats,
                "Meow" + RandomStringUtils.randomAlphabetic(5),
                Collections.singletonList("urls"),
                asList(cat, mini),
                Statuses.sold.name());

        Response responseAddPet1 = given()
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(newPet1)
                .post();
        assertEquals(200, responseAddPet1.getStatusCode());

        // удалить своего питомца в конце теста, чтобы не засорять базу
        Response deleteResponse =
                given()
                        .pathParam("Id", newPet1.getId())
                        .basePath("/pet/{Id}")
                        .accept("application/json")
                        .when()
                        .delete();
        System.out.println(deleteResponse.asString());
        DeletePetResponse deleteResponseAsClass = deleteResponse.as(DeletePetResponse.class);
        assertEquals(200, deleteResponseAsClass.getCode());
        assertNotNull(deleteResponseAsClass.getType());
        assertEquals(newPet1.getId(), Long.parseLong(deleteResponseAsClass.getMessage()));

        given()
                .basePath("/pet/" + newPet1.getId())
                .accept("application/json")
                .get()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void task5_getPetByStatusSold() {
//        Preparing test data
        Category cats = new Category(1, "cats");
        Tag mini = new Tag(4, "mini");
        Tag big = new Tag(5, "big");
        Tag cat = new Tag(1, "cat");


        //создаем первого животного
        Pet newPet1 = new Pet(
                1 + (int) (Math.random() * 999),
                cats,
                "Meow" + RandomStringUtils.randomAlphabetic(5),
                Collections.singletonList("urls"),
                asList(cat, mini),
                Statuses.sold.name());

        Response responseAddPet1 = given()
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(newPet1)
                .post();

        assertEquals(200, responseAddPet1.getStatusCode());
        System.out.println("Response for adding a new pet1: \n" + responseAddPet1.asString() + "\n");

        //создаем второго животного
        Pet newPet2 = new Pet(
                1 + (int) (Math.random() * 999),
                cats,
                "Meow" + RandomStringUtils.randomAlphabetic(5),
                Collections.singletonList("urls"),
                asList(cat, big),
                Statuses.sold.name());

        Response responseAddPet2 = given()
                .basePath("/pet")
                .contentType(ContentType.JSON)
                .body(newPet2)
                .post();

        assertEquals(200, responseAddPet2.getStatusCode());
        System.out.println("Response for adding a new pet2: \n" + responseAddPet2.asString() + "\n");

        //добавляем созданных животных в коллекцию
        List pets = Arrays.asList(newPet1, newPet2);
        System.out.println(pets.toString());

        //получаем массив животных по статусу "sold"
        Pet[] foundPetsByStatus = given()
                .basePath("/pet/findByStatus")
                .param("status", "sold")
                .accept("application/json")
                .when()
                .get()
                .as(Pet[].class);
        System.out.println("Response for getting pet by Id: \n"); // log info
        for (Pet element : foundPetsByStatus) {
            System.out.println(element);
        }

        //стримы для фильтрация - поиск наших созданных животных в массиве
        Stream<Pet> myCreatedPetsStream = pets.stream();
        long counter = myCreatedPetsStream
                .filter((x) -> {
                    Stream<Pet> myReceivedPetsStream = stream(foundPetsByStatus);
                    return myReceivedPetsStream
                            .anyMatch((y) -> x.getId() == y.getId() && Objects.equals(x.getName(), y.getName()));
                })
                .count();


        // удалить своего питомца в конце теста, чтобы не засорять базу
        for (int i = 0; i < foundPetsByStatus.length; i++) {
            Pet pet = foundPetsByStatus[i];
            Response deleteResponse =
                    given()
                            .pathParam("Id", pet.getId())
                            .basePath("/pet/{Id}")
                            .accept("application/json")
                            .when()
                            .delete();
            System.out.println(deleteResponse.asString());

            DeletePetResponse deleteResponseAsClass = deleteResponse.as(DeletePetResponse.class);
            assertEquals(200, deleteResponseAsClass.getCode());
            assertNotNull(deleteResponseAsClass.getType());
            assertEquals(pet.getId(), Long.parseLong(deleteResponseAsClass.getMessage()));
        }

        assertEquals(2, counter);
    }
}

