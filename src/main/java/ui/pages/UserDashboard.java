package ui.pages;

import api.Models.ChangeUserNameRequest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.*;

@Getter
public class UserDashboard extends BasePage<UserDashboard> {
    private SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
    private SelenideElement createNewAccount = $(Selectors.byText("➕ Create New Account"));
    private SelenideElement depositAccount = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));
    private SelenideElement tranfer = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));
    private SelenideElement nonameElement = $x("//span[@class='user-name' and text()='Noname']");


    @Override
    public String url() {
        return "/dashboard";
    }

    public UserDashboard createAccount() {
        createNewAccount.click();
        return this;
    }

    public UserDashboard depositAccount() {
        depositAccount.click();
        return this;
    }

    public UserDashboard transfer() {
        tranfer.click();
        return this;
    }

    public UserDashboard changeName() {
        nonameElement.shouldBe(Condition.exist);
        executeJavaScript("arguments[0].click();", nonameElement);
        Selenide.sleep(1000);
        return this;
    }

    public UserDashboard checkNewName(ChangeUserNameRequest changeUserNameRequest) {
        welcomeText.shouldHave(Condition.text("Welcome, " + changeUserNameRequest.getName() + "!"));
        return this;
    }

    public UserDashboard checkNewNameDoesNotChanged() {
        welcomeText.shouldHave(Condition.text("Welcome, noname!"));
        return this;
    }
}
