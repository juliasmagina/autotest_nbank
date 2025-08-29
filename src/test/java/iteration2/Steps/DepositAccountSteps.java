package iteration2.Steps;

import Models.CreateAccountResponse;
import Models.CreateUserRequest;
import Models.DepositAccountRequest;
import Models.DepositAccountResponse;
import Requests.DepostAccountRequester;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;

public class DepositAccountSteps {

    public static DepositAccountResponse deposit(CreateUserRequest user, CreateAccountResponse account) {
        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .id(account.getId())
                .balance(5000)
                .build();

        return new DepostAccountRequester(RequestSpecifications.userSpec(user.getUsername(), user.getPassword()), ResponseSpecifications.statusOk())
                .post(depositAccountRequest).extract().as(DepositAccountResponse.class);
    }
}
