package api.Steps;

import api.Models.*;
import api.Specs.RequestSpecifications;
import api.Specs.ResponseSpecifications;
import api.skeleton.Endpoint;
import api.skeleton.requesters.CrudRequester;
import api.skeleton.requesters.ValidatedCrudRequester;

import java.util.Arrays;
import java.util.List;

public class UserSteps {


    public UserSteps(CreateUserRequest user) {
    }

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

    public static List<TransactionsResponse> checkTransactions(CreateUserRequest user, CreateAccountResponse createAccountResponse) {
        TransactionsResponse[] transactionsArray = new CrudRequester(
                Endpoint.CHECK_TRANSACTIONS,
                RequestSpecifications.userSpec(user.getUsername(), user.getPassword()),
                ResponseSpecifications.statusOk())
                .get(createAccountResponse.getId())
                .extract()
                .as(TransactionsResponse[].class);

        return Arrays.asList(transactionsArray);
    }

    public static List<CreateAccountResponse> checkAccount(CreateUserRequest user) {
        return new ValidatedCrudRequester<CreateAccountResponse>(
                Endpoint.CHECK_ACCOUNT,
                RequestSpecifications.userSpec(user.getUsername(), user.getPassword()),
                ResponseSpecifications.statusOk()).getAll(CreateAccountResponse[].class);
    }

    public static ViewProfileResponse viewProfile(CreateUserRequest user) {
        return new ValidatedCrudRequester<ViewProfileResponse>(Endpoint.VIEW_PROFILE, RequestSpecifications.userSpec(user.getUsername(), user.getPassword()), ResponseSpecifications.statusOk()).get();
    }
}

