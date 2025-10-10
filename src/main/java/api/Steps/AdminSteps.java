package api.Steps;

import api.Generators.RandomModelGenerator;
import api.Models.CreateUserRequest;
import api.Models.CreateUserResponse;
import api.Specs.RequestSpecifications;
import api.Specs.ResponseSpecifications;
import api.skeleton.Endpoint;
import api.skeleton.requesters.ValidatedCrudRequester;

import java.util.List;

public class AdminSteps {
    private AdminSteps() {
    }

    public static CreateUserRequest createUser() {
        CreateUserRequest userRequest =
                RandomModelGenerator.generate(CreateUserRequest.class);
        new ValidatedCrudRequester<CreateUserResponse>(
                Endpoint.ADMIN_USER,
                RequestSpecifications.adminSpec(),
                ResponseSpecifications.entityWasCreated())
                .post(userRequest);
        return userRequest;
    }

    public static List<CreateUserResponse> getAllUsers() {
        return new ValidatedCrudRequester<CreateUserResponse>(
                Endpoint.ADMIN_USER,
                RequestSpecifications.adminSpec(),
                ResponseSpecifications.statusOk()).getAll(CreateUserResponse[].class);

    }
}
