package iteration2.ui;

import api.Models.CreateAccountResponse;
import api.Models.TYPES;
import api.Models.TransactionsResponse;
import api.Steps.UserSteps;
import common.annotations.CreatingUserAccount;
import common.annotations.Deposit;
import common.annotations.UserSession;
import common.storage.AccountStorage;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.TransferPage;
import ui.pages.UserDashboard;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferBetweenAccountsTest extends BaseUITest {

    @Test
    @UserSession(value = 2)
    @CreatingUserAccount(value = 2)
    @Deposit
    public void userCanTransferMoneyTest() {

        float initialBalance2 = AccountStorage.getAccount(2).getBalance();
        float balanceAfterDeposit = UserSteps.checkAccount(SessionStorage.getUser(1)).get(0).getBalance();

        new UserDashboard().open().transfer().getPage(TransferPage.class).sendTransfer(SessionStorage.getUser(2), AccountStorage.getAccount(2)).checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_TRANSFERRED.getMessage());

        List<CreateAccountResponse> finalAccounts1 = UserSteps.checkAccount(SessionStorage.getUser(1));
        List<CreateAccountResponse> finalAccounts2 = UserSteps.checkAccount(SessionStorage.getUser(2));

        float finalBalance1 = finalAccounts1.stream()
                .filter(acc -> acc.getId() == AccountStorage.getAccount(1).getId())
                .findFirst()
                .get()
                .getBalance();

        float finalBalance2 = finalAccounts2.stream()
                .filter(acc -> acc.getId() == AccountStorage.getAccount(2).getId())
                .findFirst()
                .get()
                .getBalance();

        float amountFloat = UserSteps.checkAccount(SessionStorage.getUser(2)).getFirst().getBalance();

        assertThat(finalBalance1).isEqualTo(balanceAfterDeposit - amountFloat);
        assertThat(finalBalance2).isEqualTo(initialBalance2 + amountFloat);

        List<TransactionsResponse> checkedTransactions1 = UserSteps.checkTransactions(SessionStorage.getUser(1), AccountStorage.getAccount(1));
        List<TransactionsResponse> checkedTransactions2 = UserSteps.checkTransactions(SessionStorage.getUser(2), AccountStorage.getAccount(2));

        assertThat(checkedTransactions1).isNotNull().isNotEmpty();
        assertThat(checkedTransactions2).isNotNull().isNotEmpty();
    }

    @Test
    @UserSession(value = 2)
    @CreatingUserAccount(value = 2)
    @Deposit
    public void userCanNotTransferInvalidAmountTest() {

        float initialBalance2 = AccountStorage.getAccount(2).getBalance();
        float balanceAfterDeposit = UserSteps.checkAccount(SessionStorage.getUser(1)).get(0).getBalance();

        new UserDashboard().open().transfer().getPage(TransferPage.class).sendTransferInvalidAmount(SessionStorage.getUser(2), AccountStorage.getAccount(2)).checkAlertMessageAndAccept(BankAlert.INVALID_TRANSFER.getMessage());

        List<CreateAccountResponse> finalAccounts1 = UserSteps.checkAccount(SessionStorage.getUser(1));
        List<CreateAccountResponse> finalAccounts2 = UserSteps.checkAccount(SessionStorage.getUser(2));

        float finalBalance1 = finalAccounts1.stream()
                .filter(acc -> acc.getId() == AccountStorage.getAccount(1).getId())
                .findFirst()
                .get()
                .getBalance();

        float finalBalance2 = finalAccounts2.stream()
                .filter(acc -> acc.getId() == AccountStorage.getAccount(2).getId())
                .findFirst()
                .get()
                .getBalance();

        assertThat(finalBalance1).isEqualTo(balanceAfterDeposit);
        assertThat(finalBalance2).isEqualTo(initialBalance2);

        List<TransactionsResponse> checkedTransactions1 = UserSteps.checkTransactions(SessionStorage.getUser(1), AccountStorage.getAccount(1));
        List<TransactionsResponse> checkedTransactions2 = UserSteps.checkTransactions(SessionStorage.getUser(2), AccountStorage.getAccount(2));

        assertThat(checkedTransactions1).noneMatch(transaction -> TYPES.TRANSFER_OUT.equals(transaction.getType()));
        assertThat(checkedTransactions2).isEmpty();
    }
}

