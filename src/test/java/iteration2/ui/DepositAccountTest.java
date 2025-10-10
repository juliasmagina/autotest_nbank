package iteration2.ui;

import api.Models.CreateAccountResponse;
import api.Models.CreateUserRequest;
import api.Models.TYPES;
import api.Models.TransactionsResponse;
import api.Steps.AdminSteps;
import api.Steps.UserSteps;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.DepositPage;
import ui.pages.UserDashboard;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class DepositAccountTest extends BaseUITest {

    @Test
    public void userCanDepositTest() {
        CreateUserRequest user = AdminSteps.createUser();
        CreateAccountResponse account = UserSteps.createAccount(user);
        String accountNumber = UserSteps.checkAccount(user).getFirst().getAccountNumber();
        authAsUser(user);

        new UserDashboard().open().depositAccount().getPage(DepositPage.class).selectAccountAndEnterAmount().checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_DEPOSIT.getMessage());

        String balance = String.valueOf(UserSteps.checkAccount(user).getFirst().getBalance());

        List<TransactionsResponse> checkedTransactions = UserSteps.checkTransactions(user, account);

        TransactionsResponse transactions = checkedTransactions.stream().filter(type -> type.getType().equals(TYPES.DEPOSIT)).findFirst().get();

        assertThat(transactions.getAmount()).isNotNull();
        String formattedAmount = String.format(Locale.US, "%.1f", transactions.getAmount());
        assertThat(formattedAmount).isEqualTo(balance);
    }

    @Test

    public void userCanNotDepositInvalidSumTest() {

        CreateUserRequest user = AdminSteps.createUser();
        CreateAccountResponse account = UserSteps.createAccount(user);
        authAsUser(user);

        new UserDashboard().open().depositAccount().getPage(DepositPage.class).selectAccountAndEnterExcessAmount().checkAlertMessageAndAccept(BankAlert.PLEASE_DEPOSIT_LESS.getMessage());

        List<TransactionsResponse> checkedTransactions = UserSteps.checkTransactions(user, account);

        assertThat(checkedTransactions).isEmpty();
        assertThat(checkedTransactions).hasSize(0);
    }
}
