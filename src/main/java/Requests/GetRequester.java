package Requests;

import io.restassured.response.ValidatableResponse;

public interface GetRequester {
    public abstract ValidatableResponse get();
}
