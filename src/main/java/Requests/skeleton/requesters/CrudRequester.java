package Requests.skeleton.requesters;

import Models.BaseModel;
import Requests.skeleton.Endpoint;
import Requests.skeleton.HttpRequest;
import Requests.skeleton.interfaces.CrudEndpointInterface;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface {
    public CrudRequester(Endpoint endpoint, RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(endpoint, requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(BaseModel baseModel) {
        var body = baseModel == null ? "" : baseModel;
        return given()
                .spec(requestSpecification)
                .body(body)
                .post(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get() {
        return given()
                .spec(requestSpecification)
                .get(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }


    public ValidatableResponse get(long accountId) {
        return given()
                .spec(requestSpecification)
                .get(endpoint.buildUrl(accountId))
                .then()
                .assertThat()
                .spec(responseSpecification);
    }


    @Override
    public ValidatableResponse update(BaseModel baseModel) {
        return given()
                .spec(requestSpecification)
                .body(baseModel)
                .put(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public Object delete(long id) {
        return null;
    }
}
