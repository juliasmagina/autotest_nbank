package iteration2.Steps;

import Models.*;
import Requests.skeleton.Endpoint;
import Requests.skeleton.requesters.CrudRequester;
import Requests.skeleton.requesters.ValidatedCrudRequester;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;
import io.restassured.response.ValidatableResponse;

import java.util.List;

public class UserSteps {

    public static DepositAccountResponse<BaseModel> deposit(CreateUserRequest user, CreateAccountResponse account) {
        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .id(account.getId())
                .balance(5000)
                .build();
        return new ValidatedCrudRequester<DepositAccountResponse<BaseModel>>
                (Endpoint.DEPOSIT,
                        RequestSpecifications.userSpec(user.getUsername(), user.getPassword()),
                        ResponseSpecifications.statusOk())
                .post(depositAccountRequest);
    }


    public static CreateAccountResponse createAccount(CreateUserRequest user) {
        return new ValidatedCrudRequester<CreateAccountResponse>
                (Endpoint.ACCOUNT, RequestSpecifications.userSpec(user.getUsername(), user.getPassword()),
                        ResponseSpecifications.entityWasCreated())
                .post(null);
    }

    public static List<TransactionsResponse> checkTransactions(CreateUserRequest user, CreateAccountResponse account) {
        ValidatableResponse response = new CrudRequester
                (Endpoint.CHECK_TRANSACTIONS,
                        RequestSpecifications.userSpec(user.getUsername(), user.getPassword()),
                        ResponseSpecifications.statusOk())
                .get(account.getId());
        return response.extract().jsonPath().getList("", TransactionsResponse.class);
    }

    public static List<CreateAccountResponse> checkAccount(CreateUserRequest user) {
        ValidatableResponse response =
                new CrudRequester(Endpoint.CHECK_ACCOUNT,
                        RequestSpecifications.userSpec(user.getUsername(), user.getPassword()),
                        ResponseSpecifications.statusOk())
                        .get();
        return response.extract()
                .jsonPath()
                .getList("", CreateAccountResponse.class);
    }
}

