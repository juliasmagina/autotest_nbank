package Requests.skeleton;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public abstract class HttpRequest {

    protected RequestSpecification requestSpecification;
    protected Endpoint endpoint;
    protected ResponseSpecification responseSpecification;

    public HttpRequest(Endpoint endpoint, RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        this.endpoint = endpoint;
        this.requestSpecification = requestSpecification;
        this.responseSpecification = responseSpecification;
    }
}
