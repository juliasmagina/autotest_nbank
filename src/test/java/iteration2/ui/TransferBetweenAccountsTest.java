package iteration2.ui;

import api.Models.*;
import api.Steps.AdminSteps;
import api.Steps.UserSteps;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.TransferPage;
import ui.pages.UserDashboard;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferBetweenAccountsTest extends BaseUITest {

    @Test
    public void userCanTransferMoneyTest() {

        CreateUserRequest user1 = AdminSteps.createUser();
        CreateAccountResponse account1 = UserSteps.createAccount(user1);
        CreateUserRequest user2 = AdminSteps.createUser();
        CreateAccountResponse account2 = UserSteps.createAccount(user2);
        float initialBalance2 = account2.getBalance();
        //User can deposit account for 5000, but user can transfer 10000, so we need 3 transactions to get enough balance
        int transactionQuantity = 3;
        for (int i = 0; i < transactionQuantity; i++) {
            DepositAccountResponse<BaseModel> deposit = UserSteps.deposit(user1, account1);
        }
        List<CreateAccountResponse> afterDepositAccounts1 = UserSteps.checkAccount(user1);
        float balanceAfterDeposit = afterDepositAccounts1.get(0).getBalance();
        authAsUser(user1);

        new UserDashboard().open().transfer().getPage(TransferPage.class).sendTransfer(user2, account2).checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_TRANSFERRED.getMessage());

        List<CreateAccountResponse> finalAccounts1 = UserSteps.checkAccount(user1);
        List<CreateAccountResponse> finalAccounts2 = UserSteps.checkAccount(user2);

        float finalBalance1 = finalAccounts1.stream()
                .filter(acc -> acc.getId() == account1.getId())
                .findFirst()
                .get()
                .getBalance();

        float finalBalance2 = finalAccounts2.stream()
                .filter(acc -> acc.getId() == account2.getId())
                .findFirst()
                .get()
                .getBalance();

        float amountFloat = UserSteps.checkAccount(user2).getFirst().getBalance();

        assertThat(finalBalance1).isEqualTo(balanceAfterDeposit - amountFloat);
        assertThat(finalBalance2).isEqualTo(initialBalance2 + amountFloat);

        List<TransactionsResponse> checkedTransactions1 = UserSteps.checkTransactions(user1, account1);
        List<TransactionsResponse> checkedTransactions2 = UserSteps.checkTransactions(user2, account2);

        assertThat(checkedTransactions1).isNotNull().isNotEmpty();
        assertThat(checkedTransactions2).isNotNull().isNotEmpty();
    }

    @Test
    public void userCanNotTransferInvalidAmountTest() {

        CreateUserRequest user1 = AdminSteps.createUser();
        CreateAccountResponse account1 = UserSteps.createAccount(user1);
        CreateUserRequest user2 = AdminSteps.createUser();
        CreateAccountResponse account2 = UserSteps.createAccount(user2);
        float initialBalance2 = account2.getBalance();
        //User can deposit account for 5000, but user can transfer 10000, so we need 3 transactions to get enough balance
        int transactionQuantity = 3;
        for (int i = 0; i < transactionQuantity; i++) {
            DepositAccountResponse<BaseModel> deposit = UserSteps.deposit(user1, account1);
        }
        List<CreateAccountResponse> afterDepositAccounts1 = UserSteps.checkAccount(user1);
        float balanceAfterDeposit = afterDepositAccounts1.get(0).getBalance();
        authAsUser(user1);

        new UserDashboard().open().transfer().getPage(TransferPage.class).sendTransferInvalidAmount(user2, account2).checkAlertMessageAndAccept(BankAlert.INVALID_TRANSFER.getMessage());

        List<CreateAccountResponse> finalAccounts1 = UserSteps.checkAccount(user1);
        List<CreateAccountResponse> finalAccounts2 = UserSteps.checkAccount(user2);

        float finalBalance1 = finalAccounts1.stream()
                .filter(acc -> acc.getId() == account1.getId())
                .findFirst()
                .get()
                .getBalance();

        float finalBalance2 = finalAccounts2.stream()
                .filter(acc -> acc.getId() == account2.getId())
                .findFirst()
                .get()
                .getBalance();

        assertThat(finalBalance1).isEqualTo(balanceAfterDeposit);
        assertThat(finalBalance2).isEqualTo(initialBalance2);

        List<TransactionsResponse> checkedTransactions1 = UserSteps.checkTransactions(user1, account1);
        List<TransactionsResponse> checkedTransactions2 = UserSteps.checkTransactions(user2, account2);

        assertThat(checkedTransactions1).noneMatch(transaction -> TYPES.TRANSFER_OUT.equals(transaction.getType()));
        assertThat(checkedTransactions2).isEmpty();
    }
}

