package Requests;

import Models.BaseModel;
import io.restassured.response.ValidatableResponse;

public interface PutRequester {

    public abstract ValidatableResponse put(BaseModel baseModel);
}
