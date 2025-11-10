package api.Steps;

import api.Models.*;
import api.Specs.RequestSpecifications;
import api.Specs.ResponseSpecifications;
import api.skeleton.Endpoint;
import api.skeleton.requesters.CrudRequester;
import api.skeleton.requesters.ValidatedCrudRequester;
import common.helpers.StepLogger;

import java.util.List;

public class UserSteps {


    public UserSteps(CreateUserRequest user) {
    }

    public static DepositAccountResponse<BaseModel> deposit(CreateUserRequest user, CreateAccountResponse account) {
        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .accountId(account.getId())
                .amount(5000)
                .description("String")
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
        return new CrudRequester(Endpoint.CHECK_TRANSACTIONS, RequestSpecifications.userSpec(user.getUsername(), user.getPassword()), ResponseSpecifications.statusOk())
                .get(createAccountResponse.getId())
                .extract()
                .jsonPath()  // Используем jsonPath() для работы с коллекциями
                .getList("", TransactionsResponse.class);
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

    public static TransferResponseNew transferWithFraudCheck(Long senderAccountId, Long receiverAccountId, float amount, CreateUserRequest user) {
        return StepLogger.log("User " + user.getUsername() + " transfers " + amount + " to " + receiverAccountId + " with fraud check", () -> {
            TransferRequest transferRequest = TransferRequest.builder()
                    .senderAccountId(senderAccountId)
                    .receiverAccountId(receiverAccountId)
                    .amount(amount)
                    .description("Test transfer with fraud check")
                    .build();

            return new ValidatedCrudRequester<TransferResponseNew>(
                    Endpoint.TRANSFER_WITH_FRAUD_CHECK,
                    RequestSpecifications.userSpec(user.getUsername(), user.getPassword()),
                    ResponseSpecifications.statusOk()).post(transferRequest);
        });
    }
}

