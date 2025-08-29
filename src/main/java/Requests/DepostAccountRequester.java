package Requests;

import Models.BaseModel;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class DepostAccountRequester extends Request implements PostRequester {
    public DepostAccountRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel baseModel) {
        return given()
                .spec(requestSpecification)
                .body(baseModel)
                .post("/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
