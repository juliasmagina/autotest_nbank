package Steps;

import Generators.RandomModelGenerator;
import Models.CreateUserRequest;
import Models.CreateUserResponse;
import Requests.skeleton.Endpoint;
import Requests.skeleton.requesters.ValidatedCrudRequester;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;

public class AdminSteps {

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
}
