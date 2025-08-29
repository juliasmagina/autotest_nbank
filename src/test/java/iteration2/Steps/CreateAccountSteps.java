package iteration2.Steps;

import Models.CreateAccountResponse;
import Models.CreateUserRequest;
import Requests.CreateAccountRequester;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;

public class CreateAccountSteps {

    public static CreateAccountResponse createAccount(CreateUserRequest user) {

        return new CreateAccountRequester(RequestSpecifications.userSpec(user.getUsername(), user.getPassword()), ResponseSpecifications.entityWasCreated())
                .post(null).extract().as(CreateAccountResponse.class);
    }
}
