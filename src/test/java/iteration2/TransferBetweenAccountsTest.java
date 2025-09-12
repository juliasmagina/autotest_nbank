package iteration2;

import Generators.RandomData;
import Models.*;
import Requests.TransferRequester;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;
import iteration2.Steps.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

public class TransferBetweenAccountsTest extends BaseTest {

    private String generatedUsername1;
    private String generatedPassword1;
    private CreateAccountResponse account1;
    private CreateAccountResponse account2;
    private CreateUserRequest user1;
    private CreateUserRequest user2;


    @BeforeEach
    public void setUp() {
        CreateUserRequest user1 = CreateUserSteps.createUser();
        CreateUserRequest user2 = CreateUserSteps.createUser();
        this.user1 = user1;
        this.user2 = user2;
        this.generatedUsername1 = user1.getUsername();
        this.generatedPassword1 = user1.getPassword();


        CreateAccountResponse account1 = CreateAccountSteps.createAccount(user1);
        CreateAccountResponse account2 = CreateAccountSteps.createAccount(user2);


        //User can deposit account for 5000, but user can transfer 10000, so we need 3 transactions to get enough balance
        int transactionQuantity = 3;

        for (int i = 0; i < transactionQuantity; i++) {
            DepositAccountResponse deposit = DepositAccountSteps.deposit(user1, account1);
        }

        this.account1 = account1;
        this.account2 = account2;

    }


    @Test
    @DisplayName("User1 can transfer to user2")
    public void userCanTransferToUser2Test() {

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(RandomData.getBalance())
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .build();

        TransferResponse transferResponse = new TransferRequester(RequestSpecifications.userSpec(generatedUsername1, generatedPassword1), ResponseSpecifications.statusOk())
                .post(transferRequest).extract().as(TransferResponse.class);

        softly.assertThat(transferRequest.getSenderAccountId()).isEqualTo(transferResponse.getSenderAccountId());
        softly.assertThat(transferRequest.getReceiverAccountId()).isEqualTo(transferResponse.getReceiverAccountId());
        softly.assertThat(transferRequest.getAmount()).isEqualTo(transferResponse.getAmount());
        softly.assertThat(transferResponse.getMessage()).isEqualTo("Transfer successful");

        List<TransactionsResponse> transactionsForUser1 = CheckTransactionsSteps.checkTransactions(user1, account1);
        List<TransactionsResponse> transactionsForUser2 = CheckTransactionsSteps.checkTransactions(user2, account2);

        softly.assertThat(transactionsForUser1).as("Transaction list should not be empty").isNotEmpty();
        softly.assertThat(transactionsForUser2).as("Transaction list should not be empty").isNotEmpty();

        softly.assertThat(transactionsForUser1.stream().filter(s -> s.getType().equals(TYPES.TRANSFER_OUT))
                .findFirst().get().getAmount()).isEqualTo(transferResponse.getAmount());
        softly.assertThat(transactionsForUser2.stream().filter(s -> s.getType().equals(TYPES.TRANSFER_IN))
                .findFirst().get().getAmount()).isEqualTo(transferResponse.getAmount());

        List<CreateAccountResponse> accountResponses1 = CheckAccountsSteps.checkAccount(user1);
        List<CreateAccountResponse> accountResponses2 = CheckAccountsSteps.checkAccount(user2);


        softly.assertThat(accountResponses1).isNotEmpty();
        softly.assertThat(accountResponses2).isNotEmpty();
        softly.assertThat(accountResponses2.getFirst().getBalance()).isEqualTo(transferResponse.getAmount());


    }


    @Test
    @DisplayName("User1 can transfer to user2 less than 10000 and check accounts that amount changed")
    public void userCanTransferToUser10000Test() {
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(10000)
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .build();

        TransferResponse transferResponse = new TransferRequester(RequestSpecifications.userSpec(generatedUsername1, generatedPassword1), ResponseSpecifications.statusOk())
                .post(transferRequest).extract().as(TransferResponse.class);

        softly.assertThat(transferRequest.getSenderAccountId()).isEqualTo(transferResponse.getSenderAccountId());
        softly.assertThat(transferRequest.getReceiverAccountId()).isEqualTo(transferResponse.getReceiverAccountId());
        softly.assertThat(transferRequest.getAmount()).isEqualTo(transferResponse.getAmount());
        softly.assertThat(transferResponse.getMessage()).isEqualTo("Transfer successful");

        List<TransactionsResponse> transactionsForUser1 = CheckTransactionsSteps.checkTransactions(user1, account1);
        List<TransactionsResponse> transactionsForUser2 = CheckTransactionsSteps.checkTransactions(user2, account2);

        softly.assertThat(transactionsForUser1).as("Transaction list should not be empty").isNotEmpty();
        softly.assertThat(transactionsForUser2).as("Transaction list should not be empty").isNotEmpty();

        softly.assertThat(transactionsForUser1.stream().filter(s -> s.getType().equals(TYPES.TRANSFER_OUT))
                .findFirst().get().getAmount()).isEqualTo(transferResponse.getAmount());
        softly.assertThat(transactionsForUser2.stream().filter(s -> s.getType().equals(TYPES.TRANSFER_IN))
                .findFirst().get().getAmount()).isEqualTo(transferResponse.getAmount());

        List<CreateAccountResponse> accountResponses1 = CheckAccountsSteps.checkAccount(user1);
        List<CreateAccountResponse> accountResponses2 = CheckAccountsSteps.checkAccount(user2);


        softly.assertThat(accountResponses1).isNotEmpty();
        softly.assertThat(accountResponses2).isNotEmpty();
        softly.assertThat(accountResponses2.getFirst().getBalance()).isEqualTo(transferResponse.getAmount());

    }


// Negative tests


    public static Stream<Arguments> invalidAmount() {
        return Stream.of(
                Arguments.of(0, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(-1, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(10001, "Transfer amount cannot exceed 10000")
        );
    }

    @MethodSource("invalidAmount")
    @ParameterizedTest
    @DisplayName("User can not transfer invalid amount + check that balance is not changed")
    public void userCanNotTransferInvalidAmountTest(int amount, String error) {
        TransferRequest transferRequest = TransferRequest.builder()
                .amount(amount)
                .senderAccountId(account1.getId())
                .receiverAccountId(account2.getId())
                .build();

        new TransferRequester(RequestSpecifications.userSpec(generatedUsername1, generatedPassword1), ResponseSpecifications.returnsBadRequest(error))
                .post(transferRequest);

        List<TransactionsResponse> transactionsForUser2 = CheckTransactionsSteps.checkTransactions(user2, account2);

        softly.assertThat(transactionsForUser2).isEmpty();

        List<CreateAccountResponse> accountResponses2 = CheckAccountsSteps.checkAccount(user2);

        softly.assertThat(accountResponses2.getFirst().getBalance()).isEqualTo(0.0f);


    }

}
