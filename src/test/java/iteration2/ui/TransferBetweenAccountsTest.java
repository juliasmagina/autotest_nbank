package iteration2.ui;

import Generators.RandomData;
import Models.*;
import Requests.skeleton.Endpoint;
import Requests.skeleton.requesters.CrudRequester;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;
import Steps.AdminSteps;
import Steps.UserSteps;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferBetweenAccountsTest {

    @BeforeAll
    public static void setUpSelenide() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://host.docker.internal:3000";
        Configuration.browserSize = "1920x1080";
        Configuration.browser = "chrome";

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));
    }

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
        String userBasicAuth = new CrudRequester(Endpoint.LOGIN,
                RequestSpecifications.unauthSpec(), ResponseSpecifications.statusOk())
                .post(LoginUserRequest.builder().username(user1.getUsername()).password(user1.getPassword()).build())
                .extract()
                .header("Authorization");
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userBasicAuth);

        Selenide.open("/dashboard");

        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();
        $(".account-selector").selectOption(1);
        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue(user2.getUsername());
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(account2.getAccountNumber());
        String amount = String.valueOf(RandomData.getAmount());
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(amount);
        $("#confirmCheck").setSelected(true);
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText.contains("✅ Successfully transferred" + amount + " to account " + account2.getAccountNumber() + "!"));
        alert.accept();

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

        float amountFloat = Float.parseFloat(amount);

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
        String userBasicAuth = new CrudRequester(Endpoint.LOGIN,
                RequestSpecifications.unauthSpec(), ResponseSpecifications.statusOk())
                .post(LoginUserRequest.builder().username(user1.getUsername()).password(user1.getPassword()).build())
                .extract()
                .header("Authorization");
        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userBasicAuth);

        Selenide.open("/dashboard");

        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();
        $(".account-selector").selectOption(1);
        $(Selectors.byAttribute("placeholder", "Enter recipient name")).setValue(user2.getUsername());
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(account2.getAccountNumber());
        String amount = "10001";
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(amount);
        $("#confirmCheck").setSelected(true);
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText.contains("❌ Error: Invalid transfer: insufficient funds or invalid accounts"));
        alert.accept();

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

