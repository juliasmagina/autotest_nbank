package Requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class CheckTransactionsRequester extends Request {
    public CheckTransactionsRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    public ValidatableResponse get(long accountId) {
        return given()
                .spec(requestSpecification)
                .pathParams("accountId", accountId)
                .get("/api/v1/accounts/{accountId}/transactions")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

}
