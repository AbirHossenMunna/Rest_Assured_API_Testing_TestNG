package user;

import Utils.Utils;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.CreateUser;
import model.UserLogin;
import org.apache.commons.configuration.ConfigurationException;
import org.testng.Assert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given;

public class user {
    Properties prop = new Properties();
    FileInputStream file;

    {
        try {
            file = new FileInputStream("./src/test/resources/config.properties");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void CallingLoginAPI() throws IOException, ConfigurationException {
        prop.load(file);
        UserLogin userLogin = new UserLogin("salman@grr.la", "1234");
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given()
                .contentType("application/json")
                .body(userLogin).
                when()
                .post("/user/login").
                then()
                .assertThat().statusCode(200).extract().response();
        JsonPath jsonPath = res.jsonPath();
        String token = jsonPath.get("token");
        System.out.println(token);
        Utils.setCollectionVariable("token", token);
    }

    public void incorrectEmail() throws IOException {
        prop.load(file);
        UserLogin userLogin = new UserLogin("salman@grr", "1234");
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given()
                .contentType("application/json")
                .body(userLogin).
                when()
                .post("/user/login").
                then()
                .assertThat().statusCode(404).extract().response();
        JsonPath response = res.jsonPath();
        Assert.assertTrue(response.get("message").toString().contains("User not found"));
    }

    public void incorrectPassword() throws IOException {
        prop.load(file);
        UserLogin userLogin = new UserLogin("salman@grr.la", "12");
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given()
                .contentType("application/json")
                .body(userLogin).
                when()
                .post("/user/login").
                then()
                .assertThat().statusCode(401).extract().response();
        JsonPath response = res.jsonPath();
        Assert.assertTrue(response.get("message").toString().contains("Password incorrect"));
    }

    public void blankPassword() throws IOException {
        prop.load(file);
        UserLogin userLogin = new UserLogin("salman@grr.la", "");
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given()
                .contentType("application/json")
                .body(userLogin).
                when()
                .post("/user/login").
                then()
                .assertThat().statusCode(401).extract().response();
        JsonPath response = res.jsonPath();
        Assert.assertTrue(response.get("message").toString().contains("Password incorrect"));
    }

    public void getUserList() throws IOException {
        prop.load(file);
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given()
                .contentType("application/json")
                .header("Authorization", prop.getProperty("token")).
                when()
                .get("/user/list").
                then()
                .assertThat().statusCode(200).extract().response();
        JsonPath response = res.jsonPath();
        Assert.assertEquals(response.get("users[0].id").toString(), "58");
        Assert.assertTrue(response.get("message").toString().contains("User list"));
    }

    public void getUserListForIncorrectToken() throws IOException {
        prop.load(file);
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given().contentType("application/json")
                .header("Authorization", "incorrect token")
                .when().get("/user/list").
                then().assertThat().statusCode(403).extract().response();
        JsonPath response = res.jsonPath();
        System.out.println(response.get("error.message").toString());
        Assert.assertTrue(response.get("error.message").toString().contains("Token expired!"));
    }

    public void getUserListForBlankToken() throws IOException {
        prop.load(file);
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given().contentType("application/json")
                .header("Authorization", "")
                .when().get("/user/list").
                then().assertThat().statusCode(401).extract().response();
        JsonPath response = res.jsonPath();
        System.out.println(response.get("error.message").toString());
        Assert.assertTrue(response.get("error.message").toString().contains("No Token Found!"));
    }

    public void CreateUser() throws IOException, ConfigurationException {
        prop.load(file);
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String phone = faker.phoneNumber().phoneNumber();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String nid = "772" + (int) Math.random() * ((9999999 - 1000000) + 1) + 9999999;
        CreateUser createUser = new CreateUser(name, email, password, phone, nid, "Customer");
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given().contentType("application/json")
                .header("Authorization", prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY", "ROADTOSDET")
                .body(createUser).
//                .body("{\n" +
//                        "    \"name\":\"" + name + "\",\n" +
//                        "    \"email\":\"" + email + "\",\n" +
//                        "    \"password\":\"" + password + "\",\n" +
//                        "    \"phone_number\":\"" + phone + "\",\n" +
//                        "    \"nid\":\"" + nid + "\",\n" +
//                        "    \"role\":\"Customer\"\n" +
//                        "}").
        when().post("/user/create").
                then().assertThat().statusCode(201).extract().response();
        JsonPath response = res.jsonPath();
        System.out.println(response.get("message").toString());
        Assert.assertTrue(response.get("message").toString().contains("User created successfully"));
        String id = response.get("user.id").toString();
        System.out.println(id);
        Utils.setCollectionVariable("id", id);
    }

    public void AlreadyExistsUser() throws IOException {
        prop.load(file);
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given().contentType("application/json")
                .header("Authorization", prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY", "ROADTOSDET")
                .body("{\n" +
                        "         \"name\": \"Dr. Jenee Schroeder\",\n" +
                        "         \"email\": \"pearline.donnelly@hotmail.com\",\n" +
                        "         \"password\": \"wbblz1v67\",\n" +
                        "          \"phone_number\": \"1-682-264-4410 x156\",\n" +
                        "          \"nid\": \"77209999999\",\n" +
                        "          \"role\": \"Customer\"\n" +
                        "}").
                when().post("/user/create")
                .then().assertThat().statusCode(208).extract().response();
        JsonPath response = res.jsonPath();
        System.out.println(response.get("message").toString());
        Assert.assertTrue(response.get("message").toString().contains("User already exists"));
    }

    public void searchUser() throws IOException {
        prop.load(file);
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given().contentType("application/json")
                .header("Authorization", prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY", "ROADTOSDET")
                .when().get("/user/search?id=" + prop.getProperty("id"))
                .then().assertThat().statusCode(200).extract().response();
        JsonPath response = res.jsonPath();
        System.out.println(response.get("user.id").toString());
        Assert.assertEquals(response.get("user.id").toString(), prop.getProperty("id"));
    }

    public void searchUserForInvalidId() throws IOException {
        prop.load(file);
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given().contentType("application/json")
                .header("Authorization", prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY", "ROADTOSDET")
                .when().get("/user/search?id=" + "947598347598347")
                .then().assertThat().statusCode(200).extract().response();
        JsonPath response = res.jsonPath();
        System.out.println(response.get("user.id") == null);
        Assert.assertTrue(response.get("user.id") == null);
    }

    public void updateUser() throws IOException {
        prop.load(file);
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given().contentType("application/json")
                .header("Authorization", prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY", "ROADTOSDET")
                .body("{\n" +
                        "            \"name\": \"Leo Messi\",\n" +
                        "            \"email\": \"Lm10@gmail.com\",\n" +
                        "            \"password\": \"123456789\",\n" +
                        "            \"phone_number\": \"01835153875\",\n" +
                        "            \"nid\": \"772132554\",\n" +
                        "            \"role\": \"Customer\"\n" +
                        "}")
                .when().put("/user/update/" + prop.getProperty("id"))
                .then().assertThat().statusCode(200).extract().response();
        JsonPath response = res.jsonPath();
        Assert.assertTrue(response.get("message").toString().contains("User updated"));
    }
    public void updateUserPhoneNumber() throws IOException {
        prop.load(file);
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given().contentType("application/json")
                .header("Authorization", prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY", "ROADTOSDET")
                .body("{\n" +
                        "\n" +
                        "           \"phone_number\":\"01732132556\" \n" +
                        "}")
                .when().patch("/user/update/" + prop.getProperty("id"))
                .then().assertThat().statusCode(200).extract().response();
        JsonPath response = res.jsonPath();
        Assert.assertEquals(response.get("user.phone_number").toString(), "01732132556");
    }
    public void deleteUser() throws IOException {
        prop.load(file);
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given().contentType("application/json")
                .header("Authorization", prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY", "ROADTOSDET")
                .when().delete("/user/delete/" + prop.getProperty("id"))
                .then().assertThat().statusCode(200).extract().response();
        JsonPath response = res.jsonPath();
        Assert.assertEquals(response.get("message").toString(), "User deleted successfully");
    }
    public void alreadyDeleteUser() throws IOException {
        prop.load(file);
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given().contentType("application/json")
                .header("Authorization", prop.getProperty("token"))
                .header("X-AUTH-SECRET-KEY", "ROADTOSDET")
                .when().delete("/user/delete/" + prop.getProperty("id"))
                .then().assertThat().statusCode(404).extract().response();
        JsonPath response = res.jsonPath();
        Assert.assertEquals(response.get("message").toString(), "User not found");
    }
}
