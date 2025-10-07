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
import java.util.Locale;
import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DepositAccountTest {

    @BeforeAll
    public static void setUpSelenide() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://host.docker.internal:3000";
        Configuration.browserSize = "1920x1080";
        Configuration.browser = "chrome";
        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
    }

    @Test
    public void userCanDepositTest() {
        CreateUserRequest user = AdminSteps.createUser();
        CreateAccountResponse account = UserSteps.createAccount(user);
        String accountNumber = UserSteps.checkAccount(user).getFirst().getAccountNumber();
        String userBasicAuth = new CrudRequester(Endpoint.LOGIN, RequestSpecifications.unauthSpec(), ResponseSpecifications.statusOk()).post(LoginUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build()).extract().header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userBasicAuth);

        Selenide.open("/dashboard");

        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();
        $(".account-selector").selectOption(1);
        String balance = String.valueOf(RandomData.getBalance());
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(balance);
        $(Selectors.byText("\uD83D\uDCB5 Deposit")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText.contains("✅ Successfully deposited " + balance + " to account " + accountNumber + " !"));
        alert.accept();

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
        String userBasicAuth = new CrudRequester(Endpoint.LOGIN, RequestSpecifications.unauthSpec(), ResponseSpecifications.statusOk()).post(LoginUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build()).extract().header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userBasicAuth);

        Selenide.open("/dashboard");

        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();
        $(".account-selector").selectOption(1);
        String balance = "5001";
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(balance);
        $(Selectors.byText("\uD83D\uDCB5 Deposit")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText.contains("❌ Please deposit less or equal to 5000$."));
        alert.accept();

        List<TransactionsResponse> checkedTransactions = UserSteps.checkTransactions(user, account);

        assertThat(checkedTransactions).isEmpty();
        assertThat(checkedTransactions).hasSize(0);
    }
}
