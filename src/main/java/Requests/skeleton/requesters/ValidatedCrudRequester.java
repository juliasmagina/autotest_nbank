package Requests.skeleton.requesters;

import Models.BaseModel;
import Requests.skeleton.Endpoint;
import Requests.skeleton.HttpRequest;
import Requests.skeleton.interfaces.CrudEndpointInterface;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface {
    private CrudRequester crudRequester;

    public ValidatedCrudRequester(Endpoint endpoint, RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(endpoint, requestSpecification, responseSpecification);
        this.crudRequester = new CrudRequester(endpoint, requestSpecification, responseSpecification);
    }

    @Override
    public T post(BaseModel baseModel) {
        return (T) (crudRequester.post(baseModel).extract().as(endpoint.getResponseModel()));
    }

    @Override
    public T get() {
        return (T) crudRequester.get().extract().as(endpoint.getResponseModel());
    }

    public T get(long accountId) {
        return (T) crudRequester.get(accountId).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T update(BaseModel baseModel) {
        return (T) crudRequester.update(baseModel).extract().as(endpoint.getResponseModel());
    }

    @Override
    public Object delete(long id) {
        return null;
    }
}
