package iteration2.Steps;

import Generators.RandomData;
import Models.CreateUserRequest;
import Models.CreateUserResponse;
import Models.ROLES;
import Requests.AdminCreateUserRequester;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;

public class CreateUserSteps {

    public static CreateUserRequest createUser() {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(ROLES.USER.toString())
                .build();


        new AdminCreateUserRequester(
                RequestSpecifications.adminSpec(),
                ResponseSpecifications.entityWasCreated()
        ).post(userRequest).extract().as(CreateUserResponse.class);


        return userRequest;
    }
}
