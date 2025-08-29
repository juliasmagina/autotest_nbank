package iteration2;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class DepositAccountTest {
    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter())
        );
    }


    @Test
    @DisplayName("Admin Authorization to get Basic Auth")
    public void adminCanGenerateAuthTokenTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "username": "admin",
                          "password": "admin"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=");
    }


    @Test
    @DisplayName("Admin creates a new user and the user authorization")
    public void adminCreatesNewUserTestAndUserAuthTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                          "username": "Kate12368",
                          "password": "User123$",
                          "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED);


        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "username": "Kate12368",
                          "password": "User123$"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);


    }

// Auth Token Basic user17890 S2F0ZTEyMzY4OlVzZXIxMjMk

    @Test
    @DisplayName("User can create an account")
    public void userCanCreateAccountTest() {

        // Creation of account
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic S2F0ZTEyMzY4OlVzZXIxMjMk")
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("balance", Matchers.equalTo(0.0F));
    }


//13

    @Test
    @DisplayName("User can deposit an account")
    public void userCanDepositAccountTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic S2F0ZTEyMzY4OlVzZXIxMjMk")
                .body("""
                        {
                           "id": 1,
                           "balance": 1000
                         }
                        """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.notNullValue());
    }

    @Test
    @DisplayName("User can check his deposit on account")
    public void userCanCheckAccountTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic S2F0ZTEyMzY4OlVzZXIxMjMk")
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.notNullValue());
    }

    @Test
    @DisplayName("User can check transactions")
    public void userCanCheckTransactionTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic S2F0ZTEyMzY4OlVzZXIxMjMk")
                .get("http://localhost:4111/api/v1/accounts/1/transactions")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.notNullValue());
    }


    @Test
    @DisplayName("User can deposit an account for 5000 and check that balance increased")
    public void userCanDepositAccountFor5000Test() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic S2F0ZTEyMzY4OlVzZXIxMjMk")
                .body("""
                        {
                           "id": 1,
                           "balance": 1000
                         }
                        """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.notNullValue());

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic S2F0ZTEyMzY4OlVzZXIxMjMk")
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.notNullValue());
    }

    // Negative tests

    public static Stream<Arguments> depositInvalidData() {
        return Stream.of(
                Arguments.of(1, 0, "Invalid account or amount"),
                Arguments.of(1, -1, "Invalid account or amount"),
                Arguments.of(1, 5001, "Deposit amount exceeds the 5000 limit")
        );
    }

    @MethodSource("depositInvalidData")
    @ParameterizedTest
    @DisplayName("User can not deposit invalid amount + check that balance is not changed")
    public void userCanNotDepositAccountInvalidDataTest(int id, int balance, String error) {

        String requestBody = String.format(
                """
                        {
                          "id": %s,
                          "balance": %s
                        }
                        """, id, balance);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic S2F0ZTEyMzY4OlVzZXIxMjMk")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo(error));

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic S2F0ZTEyMzY4OlVzZXIxMjMk")
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.notNullValue());

    }


    @Test
    @DisplayName("User can not Deposit an Account Which Belong To Another User")
    public void userCanNotDepositAccountWhichBelongToAnotherUserTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic S2F0ZTEyMzY4OlVzZXIxMjMk")
                .body("""
                        {
                           "id": 2,
                           "balance": 1000
                         }
                        """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body(containsString("Unauthorized access to account"));
    }

    @Test
    @DisplayName("User can not Deposit a non-existing Account")
    public void userCanNotDepositNonExistingAccount() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic S2F0ZTEyMzY4OlVzZXIxMjMk")
                .body("""
                        {
                           "id": 223,
                           "balance": 1000
                         }
                        """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body(containsString("Unauthorized access to account"));
    }

}
