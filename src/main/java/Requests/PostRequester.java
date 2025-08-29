package Requests;

import Models.BaseModel;
import io.restassured.response.ValidatableResponse;

public interface PostRequester {

    public abstract ValidatableResponse post(BaseModel baseModel);
}
