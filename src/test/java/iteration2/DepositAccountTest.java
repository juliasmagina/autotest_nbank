package iteration2;

import Generators.RandomData;
import Models.*;
import Requests.*;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;
import iteration2.Steps.CreateAccountSteps;
import iteration2.Steps.CreateUserSteps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class DepositAccountTest extends BaseTest {

    private String generatedUsername;
    private String generatedPassword;
    private CreateAccountResponse createAccountResponse;

    @BeforeEach
    public void setUp() {
        CreateUserRequest user = CreateUserSteps.createUser();
        this.generatedUsername = user.getUsername();
        this.generatedPassword = user.getPassword();
        this.createAccountResponse = CreateAccountSteps.createAccount(user);

    }


    @Test
    @DisplayName("User can deposit an account")
    public void userCanDepositAnAccountTest() {

        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .id(createAccountResponse.getId())
                .balance(RandomData.getBalance())
                .build();

        DepositAccountResponse depositAccountResponse = new DepostAccountRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .post(depositAccountRequest).extract().as(DepositAccountResponse.class);

        softly.assertThat(depositAccountRequest.getId()).isEqualTo(depositAccountResponse.getId());
        softly.assertThat(depositAccountRequest.getBalance()).isEqualTo(depositAccountResponse.getBalance());
        softly.assertThat(createAccountResponse.getAccountNumber()).isEqualTo(depositAccountResponse.getAccountNumber());


        List<TransactionsResponse> transactions = new CheckTransactionsRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get(createAccountResponse.getId())
                .extract()
                .jsonPath()  // Используем jsonPath() для работы с коллекциями
                .getList("", TransactionsResponse.class);

        softly.assertThat(transactions)
                .as("Transaction list should not be empty")
                .isNotEmpty();

        softly.assertThat(transactions.getFirst().getAmount())
                .isEqualTo(depositAccountResponse.getBalance());

        List<CreateAccountResponse> accountResponses = new CheckAccountRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
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

        DepositAccountResponse depositAccountResponse = new DepostAccountRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .post(depositAccountRequest).extract().as(DepositAccountResponse.class);

        softly.assertThat(depositAccountRequest.getId()).isEqualTo(depositAccountResponse.getId());
        softly.assertThat(depositAccountRequest.getBalance()).isEqualTo(depositAccountResponse.getBalance());
        softly.assertThat(createAccountResponse.getAccountNumber()).isEqualTo(depositAccountResponse.getAccountNumber());


        List<TransactionsResponse> transactions = new CheckTransactionsRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get(createAccountResponse.getId())
                .extract()
                .jsonPath()  // Используем jsonPath() для работы с коллекциями
                .getList("", TransactionsResponse.class);

        softly.assertThat(transactions)
                .as("Transaction list should not be empty")
                .isNotEmpty();

        softly.assertThat(transactions.getFirst().getAmount())
                .isEqualTo(depositAccountResponse.getBalance());

        List<CreateAccountResponse> accountResponses = new CheckAccountRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
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

        new DepostAccountRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.returnsBadRequest(error))
                .post(depositAccountRequest);


        List<TransactionsResponse> transactions = new CheckTransactionsRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get(createAccountResponse.getId())
                .extract()
                .jsonPath()  // Используем jsonPath() для работы с коллекциями
                .getList("", TransactionsResponse.class);

        softly.assertThat(transactions)
                .as("Transaction list should  be empty")
                .isEmpty();

        List<CreateAccountResponse> accountResponses = new CheckAccountRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
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

        new AdminCreateUserRequester(
                RequestSpecifications.adminSpec(),
                ResponseSpecifications.entityWasCreated())
                .post(secondUserRequest);

        CreateAccountResponse secondUserAccount = new CreateAccountRequester(
                RequestSpecifications.userSpec(secondUserRequest.getUsername(), secondUserRequest.getPassword()),
                ResponseSpecifications.entityWasCreated())
                .post(null).extract().as(CreateAccountResponse.class);

        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .id(secondUserAccount.getId())
                .balance(RandomData.getBalance())
                .build();

        new DepostAccountRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.returnsForbidden("Unauthorized access to account"))
                .post(depositAccountRequest);

        List<CreateAccountResponse> updatedAccount = new CheckAccountRequester(
                RequestSpecifications.userSpec(secondUserRequest.getUsername(), secondUserRequest.getPassword()),
                ResponseSpecifications.statusOk())
                .get()
                .extract()
                .jsonPath().getList("", CreateAccountResponse.class);

        softly.assertThat(updatedAccount.getFirst().getBalance()).isEqualTo(secondUserAccount.getBalance());

    }

    @Test
    @DisplayName("User can not Deposit a non-existing Account")
    public void userCanNotDepositNonExistingAccount() {

        long nonExistingAccountId = Integer.MAX_VALUE + new Random().nextInt(1000) + 1L;

        DepositAccountRequest depositAccountRequest = DepositAccountRequest.builder()
                .id(createAccountResponse.getId())
                .balance(nonExistingAccountId)
                .build();


    }

}
