package api.skeleton.requesters;

import api.Models.BaseModel;
import api.skeleton.Endpoint;
import api.skeleton.HttpRequest;
import api.skeleton.interfaces.CrudEndpointInterface;
import api.skeleton.interfaces.GetAllEndpointInterface;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.Arrays;
import java.util.List;

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface, GetAllEndpointInterface {
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

    @Override
    public List<T> getAll(Class<?> clazz) {
        T[] array = (T[]) crudRequester.getAll(clazz).extract().as(clazz);
        return Arrays.asList(array);
    }


}
