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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class TransferBetweenAccountsTest {
    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter())
        );
    }

    @Test
    @DisplayName("Admin creates a new user1 nd get Auth Basic1")
    public void adminCreatesNewUser1TestAndUser1AuthTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                          "username": "User1",
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
                          "username": "User1",
                          "password": "User123$"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);


    }

    // Authorization: Basic VXNlcjE6VXNlcjEyMyQ=

    @Test
    @DisplayName("Admin creates a new user2 and get Auth Basic2")
    public void adminCreatesNewUser2andAuthUser2Test() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .body("""
                        {
                          "username": "User2",
                          "password": "User123$",
                          "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED);
    }

    // Authorization: Basic VXNlcjI6VXNlcjEyMyQ=


    @ParameterizedTest
    @CsvSource({"Basic VXNlcjI6VXNlcjEyMyQ=", "Basic VXNlcjE6VXNlcjEyMyQ="})
    @DisplayName("User1 and user2 can create accounts")
    public void userCanCreateAccountsTest(String basicAuth) {

        // Creation of account
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", basicAuth)
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("balance", Matchers.equalTo(0.0F));
    }


    @Test
    @DisplayName("User1 can deposit an account")
    public void userCanDepositAccountTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjI6VXNlcjEyMyQ=")
                .body("""
                        {
                           "id": 2,
                           "balance": 5000
                         }
                        """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.notNullValue());
    }

    @Test
    @DisplayName("User1 can transfer to user2")
    public void userCanTransferToUser2Test() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjI6VXNlcjEyMyQ=")
                .body("""
                         {
                           "senderAccountId": 2,
                           "receiverAccountId": 3,
                           "amount": 500
                         }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("message", Matchers.containsString("Transfer successful"))
                .body("senderAccountId", Matchers.equalTo(2))
                .body("receiverAccountId", Matchers.equalTo(3));

    }

    @ParameterizedTest
    @CsvSource({"Basic VXNlcjI6VXNlcjEyMyQ=", "Basic VXNlcjE6VXNlcjEyMyQ="})
    @DisplayName("User1 and user2 can check accounts")
    public void usersCanCheckAccountsTest(String basicAuth) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", basicAuth)
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.notNullValue());
    }


    public static Stream<Arguments> usersData() {
        return Stream.of(
                Arguments.of(2, "Basic VXNlcjI6VXNlcjEyMyQ="),
                Arguments.of(3, "Basic VXNlcjE6VXNlcjEyMyQ=")
        );
    }


    @MethodSource("usersData")
    @ParameterizedTest
    @DisplayName("User1 and user2 can check transactions")
    public void usersCanCheckTransactionsTest(int accountId, String basicAuth) {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", basicAuth)
                .pathParam("accountId", accountId)
                .get("http://localhost:4111/api/v1/accounts/{accountId}/transactions")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.notNullValue());
    }


    @Test
    @DisplayName("User1 can transfer to user2 less than 10000 and check accounts that amount changed")
    public void userCanTransferToUser10000Test() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjI6VXNlcjEyMyQ=")
                .body("""
                         {
                           "senderAccountId": 2,
                           "receiverAccountId": 3,
                           "amount": 10000
                         }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("message", Matchers.containsString("Transfer successful"))
                .body("senderAccountId", Matchers.equalTo(2))
                .body("receiverAccountId", Matchers.equalTo(3));

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjI6VXNlcjEyMyQ=")
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.notNullValue());

    }


    @Test
    @DisplayName("User1 can transfer Between Accounts Of One User (2 steps)")
    public void Step1CreateTheSecondAccTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjI6VXNlcjEyMyQ=")
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("balance", Matchers.equalTo(0.0F));
    }


    @Test
    @DisplayName("User1 can transfer Between Accounts Of One User (2 steps) + check the balance")
    public void Step2TransferBetweenAccountsOfOneUserTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjI6VXNlcjEyMyQ=")
                .body("""
                         {
                           "senderAccountId": 2,
                           "receiverAccountId": 4,
                           "amount": 100
                         }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("message", Matchers.containsString("Transfer successful"))
                .body("senderAccountId", Matchers.equalTo(2))
                .body("receiverAccountId", Matchers.equalTo(4));

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjI6VXNlcjEyMyQ=")
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.notNullValue());


    }



// Negative tests


    @Test
    @DisplayName("User1 can not transfer to user2 more than balance + check that balance is not changed")
    public void userCanNotTransMoreThanBalanceTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjI6VXNlcjEyMyQ=")
                .body("""
                         {
                           "senderAccountId": 2,
                           "receiverAccountId": 3,
                           "amount": 500
                         }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Invalid transfer: insufficient funds or invalid accounts"));

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjI6VXNlcjEyMyQ=")
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.notNullValue());


    }


    @Test
    @DisplayName("User1 can not transfer to non-existing account")
    public void user1CanNotTransToNonExistingAccountTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjI6VXNlcjEyMyQ=")
                .body("""
                         {
                           "senderAccountId": 2,
                           "receiverAccountId": 323,
                           "amount": 500
                         }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString("Invalid transfer: insufficient funds or invalid accounts"));

    }


    public static Stream<Arguments> invalidAmount() {
        return Stream.of(
                Arguments.of(0, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(-1, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(10001, "Transfer amount cannot exceed 10000")
        );
    }

    @MethodSource("invalidAmount")
    @ParameterizedTest
    @DisplayName("User can not transfer invalid amount + check that balance is not changed")
    public void userCanNotTransferInvalidAmountTest(int amount, String error) {
        String requestBody = String.format(
                """
                        {
                          "senderAccountId": 2,
                          "receiverAccountId": 323,
                          "amount": %s
                        }
                        
                        """, amount);
        //создание аккаунта юзера

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjI6VXNlcjEyMyQ=")
                .body(requestBody)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.equalTo(error));
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjI6VXNlcjEyMyQ=")
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("balance", Matchers.notNullValue());

    }

}
