package Requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class CheckAccountRequester extends Request implements GetRequester {


    public CheckAccountRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse get() {
        return given()
                .spec(requestSpecification)
                .get("/api/v1/customer/accounts")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
