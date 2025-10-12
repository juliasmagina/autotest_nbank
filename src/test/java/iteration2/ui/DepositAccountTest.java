package iteration2.ui;

import api.Models.TYPES;
import api.Models.TransactionsResponse;
import api.Steps.UserSteps;
import common.annotations.CreatingUserAccount;
import common.annotations.UserSession;
import common.storage.AccountStorage;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.DepositPage;
import ui.pages.UserDashboard;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class DepositAccountTest extends BaseUITest {

    @Test
    @UserSession
    @CreatingUserAccount
    public void userCanDepositTest() {

        new UserDashboard().open().depositAccount().getPage(DepositPage.class).selectAccountAndEnterAmount().checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_DEPOSIT.getMessage());

        String balance = String.valueOf(UserSteps.checkAccount(SessionStorage.getUser()).getFirst().getBalance());

        List<TransactionsResponse> checkedTransactions = UserSteps.checkTransactions(SessionStorage.getUser(), AccountStorage.getAccount());

        TransactionsResponse transactions = checkedTransactions.stream().filter(type -> type.getType().equals(TYPES.DEPOSIT)).findFirst().get();

        assertThat(transactions.getAmount()).isNotNull();
        String formattedAmount = String.format(Locale.US, "%.1f", transactions.getAmount());
        assertThat(formattedAmount).isEqualTo(balance);
    }

    @Test
    @UserSession
    @CreatingUserAccount
    public void userCanNotDepositInvalidSumTest() {

        new UserDashboard().open().depositAccount().getPage(DepositPage.class).selectAccountAndEnterExcessAmount().checkAlertMessageAndAccept(BankAlert.PLEASE_DEPOSIT_LESS.getMessage());

        List<TransactionsResponse> checkedTransactions = UserSteps.checkTransactions(SessionStorage.getUser(), AccountStorage.getAccount());

        assertThat(checkedTransactions).isEmpty();
        assertThat(checkedTransactions).hasSize(0);
    }
}
