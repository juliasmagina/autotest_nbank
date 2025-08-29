package iteration2.Steps;

import Models.CreateAccountResponse;
import Models.CreateUserRequest;
import Requests.CheckAccountRequester;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;

import java.util.List;

public class CheckAccountsSteps {

    public static List<CreateAccountResponse> checkAccount(CreateUserRequest user) {
        return new CheckAccountRequester(RequestSpecifications.userSpec(user.getUsername(), user.getPassword()), ResponseSpecifications.statusOk())
                .get()
                .extract()
                .jsonPath()
                .getList("", CreateAccountResponse.class);

    }
}
