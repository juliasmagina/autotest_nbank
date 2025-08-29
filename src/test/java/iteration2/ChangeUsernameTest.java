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

public class ChangeUsernameTest {
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
                          "username": "User123",
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
                          "username": "User123",
                          "password": "User123$"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);


    }

    // Authorization: Basic VXNlcjEyMzpVc2VyMTIzJA==

    @Test
    @DisplayName("User can view his profile")
    public void userCanViewProfileTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjEyMzpVc2VyMTIzJA==")
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("username", Matchers.equalTo("User123"));
    }

    @Test
    @DisplayName("User can change his username for valid")
    public void userCanChangeHisUserValidNameTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjEyMzpVc2VyMTIzJA==")
                .body("""
                        {
                          "name": "New Name"
                        }
                        """)
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("message", Matchers.equalTo("Profile updated successfully"));
    }

    @Test
    @DisplayName("User can view his updated profile")
    public void userCanViewUpdatedProfileTest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjEyMzpVc2VyMTIzJA==")
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("name", Matchers.equalTo("New Name"));
    }

    // negative

    public static Stream<Arguments> InvalidNames() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("Ira"),
                Arguments.of("Ira Ira Ira"),
                Arguments.of("Ira Ira123"),
                Arguments.of("Ira Ira$!")
        );
    }

    @MethodSource("InvalidNames")
    @ParameterizedTest
    @DisplayName("User can not change for invalid name + check that name is not changed")
    public void userCanNotDepositAccountInvalidDataTest(String name) {
        String requestBody = String.format(
                """
                        {
                          "name": %s
                        }
                        
                        """, name);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjEyMzpVc2VyMTIzJA==")
                .body(requestBody)
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Basic VXNlcjEyMzpVc2VyMTIzJA==")
                .get("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("name", Matchers.equalTo("New Name"));
    }

}
