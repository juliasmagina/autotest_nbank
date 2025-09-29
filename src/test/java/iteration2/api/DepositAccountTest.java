package iteration2.api;

import Generators.RandomData;
import Models.*;
import Models.comparison.ModelAssertions;
import Requests.skeleton.Endpoint;
import Requests.skeleton.requesters.CrudRequester;
import Requests.skeleton.requesters.ValidatedCrudRequester;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;
import Steps.AdminSteps;
import Steps.UserSteps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

public class DepositAccountTest extends BaseTest {

    private String generatedUsername;
    private String generatedPassword;
    private CreateAccountResponse createAccountResponse;

    @BeforeEach
    public void setUp() {
        CreateUserRequest user = AdminSteps.createUser();
        this.generatedUsername = user.getUsername();
        this.generatedPassword = user.getPassword();
        this.createAccountResponse = UserSteps.createAccount(user);

    }


    @Test
    @DisplayName("User can deposit an account")
    public void userCanDepositAnAccountTest() {

        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .id(createAccountResponse.getId())
                .balance(RandomData.getBalance())
                .build();

        DepositAccountResponse depositAccountResponse = new ValidatedCrudRequester<DepositAccountResponse>(Endpoint.DEPOSIT,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .post(depositAccountRequest);

        ModelAssertions.assertThatModels(depositAccountRequest, depositAccountResponse).match();
        softly.assertThat(createAccountResponse.getAccountNumber()).isEqualTo(depositAccountResponse.getAccountNumber());


        List<TransactionsResponse> transactions = new CrudRequester(Endpoint.CHECK_TRANSACTIONS,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get(createAccountResponse.getId())
                .extract()
                .jsonPath()  // Используем jsonPath() для работы с коллекциями
                .getList("", TransactionsResponse.class);

        softly.assertThat(transactions)
                .as("Transaction list should not be empty")
                .isNotEmpty();

        softly.assertThat(transactions.getFirst().getAmount())
                .isEqualTo(depositAccountResponse.getBalance());

        List<CreateAccountResponse> accountResponses = new CrudRequester(Endpoint.CHECK_ACCOUNT,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get()
                .extract()
                .jsonPath()
                .getList("", CreateAccountResponse.class);

        softly.assertThat(accountResponses).isNotEmpty();
        softly.assertThat(accountResponses.getFirst().getBalance()).isEqualTo(depositAccountResponse.getBalance());
        softly.assertThat(accountResponses.getFirst().getId()).isEqualTo(depositAccountResponse.getId());
        softly.assertThat(accountResponses.getFirst().getAccountNumber()).isEqualTo(depositAccountResponse.getAccountNumber());

    }


    @Test
    @DisplayName("User can deposit an account for 5000 and check that balance increased")
    public void userCanDepositAccountFor5000Test() {

        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .id(createAccountResponse.getId())
                .balance(5000)
                .build();

        DepositAccountResponse depositAccountResponse = new ValidatedCrudRequester<DepositAccountResponse>(Endpoint.DEPOSIT,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .post(depositAccountRequest);

        ModelAssertions.assertThatModels(depositAccountRequest, depositAccountResponse).match();
        softly.assertThat(createAccountResponse.getAccountNumber()).isEqualTo(depositAccountResponse.getAccountNumber());


        List<TransactionsResponse> transactions = new CrudRequester(Endpoint.CHECK_TRANSACTIONS,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get(createAccountResponse.getId())
                .extract()
                .jsonPath()  // Используем jsonPath() для работы с коллекциями
                .getList("", TransactionsResponse.class);

        softly.assertThat(transactions)
                .as("Transaction list should not be empty")
                .isNotEmpty();

        softly.assertThat(transactions.getFirst().getAmount())
                .isEqualTo(depositAccountResponse.getBalance());

        List<CreateAccountResponse> accountResponses = new CrudRequester(Endpoint.CHECK_ACCOUNT,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get()
                .extract()
                .jsonPath()
                .getList("", CreateAccountResponse.class);

        softly.assertThat(accountResponses).isNotEmpty();
        softly.assertThat(accountResponses.getFirst().getBalance()).isEqualTo(depositAccountResponse.getBalance());
        softly.assertThat(accountResponses.getFirst().getId()).isEqualTo(depositAccountResponse.getId());
        softly.assertThat(accountResponses.getFirst().getAccountNumber()).isEqualTo(depositAccountResponse.getAccountNumber());

    }

    // Negative tests

    public static Stream<Arguments> depositInvalidData() {
        return Stream.of(
                Arguments.of(0, "Invalid account or amount"),
                Arguments.of(-1, "Invalid account or amount"),
                Arguments.of(5001, "Deposit amount exceeds the 5000 limit")
        );
    }

    @MethodSource("depositInvalidData")
    @ParameterizedTest
    @DisplayName("User can not deposit invalid amount + check that balance is not changed")
    public void userCanNotDepositAccountInvalidDataTest(int balance, String error) {

        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .id(createAccountResponse.getId())
                .balance(balance)
                .build();

        new CrudRequester(Endpoint.DEPOSIT, RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.returnsBadRequest(error))
                .post(depositAccountRequest);


        List<TransactionsResponse> transactions = new CrudRequester(Endpoint.CHECK_TRANSACTIONS, RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get(createAccountResponse.getId())
                .extract()
                .jsonPath()  // Используем jsonPath() для работы с коллекциями
                .getList("", TransactionsResponse.class);

        softly.assertThat(transactions)
                .as("Transaction list should  be empty")
                .isEmpty();

        List<CreateAccountResponse> accountResponses = new CrudRequester(Endpoint.CHECK_ACCOUNT,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get()
                .extract()
                .jsonPath()
                .getList("", CreateAccountResponse.class);

        softly.assertThat(accountResponses).isNotEmpty();
        softly.assertThat(accountResponses.getFirst().getBalance()).isEqualTo(createAccountResponse.getBalance());
        softly.assertThat(accountResponses.getFirst().getId()).isEqualTo(createAccountResponse.getId());
        softly.assertThat(accountResponses.getFirst().getAccountNumber()).isEqualTo(createAccountResponse.getAccountNumber());


    }


    @Test
    @DisplayName("User can not Deposit an Account Which Belong To Another User")
    public void userCanNotDepositAccountWhichBelongToAnotherUserTest() {

        CreateUserRequest secondUserRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(ROLES.USER.toString())
                .build();

        new CrudRequester(Endpoint.ADMIN_USER,
                RequestSpecifications.adminSpec(),
                ResponseSpecifications.entityWasCreated())
                .post(secondUserRequest);

        CreateAccountResponse secondUserAccount = new ValidatedCrudRequester<CreateAccountResponse>(
                Endpoint.ACCOUNT,
                RequestSpecifications.userSpec(secondUserRequest.getUsername(), secondUserRequest.getPassword()),
                ResponseSpecifications.entityWasCreated())
                .post(null);

        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .id(secondUserAccount.getId())
                .balance(RandomData.getBalance())
                .build();

        new CrudRequester(Endpoint.DEPOSIT,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.returnsForbidden("Unauthorized access to account"))
                .post(depositAccountRequest);

        List<CreateAccountResponse> updatedAccount = new CrudRequester(
                Endpoint.CHECK_ACCOUNT,
                RequestSpecifications.userSpec(secondUserRequest.getUsername(), secondUserRequest.getPassword()),
                ResponseSpecifications.statusOk())
                .get()
                .extract()
                .jsonPath().getList("", CreateAccountResponse.class);

        softly.assertThat(updatedAccount.getFirst().getBalance()).isEqualTo(secondUserAccount.getBalance());

    }


}
