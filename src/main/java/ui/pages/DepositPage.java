package ui.pages;

import api.Generators.RandomData;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class DepositPage extends BasePage<DepositPage> {
    private SelenideElement selectAccount = $(".account-selector");
    private SelenideElement enterAmount = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement depositButton = $(Selectors.byText("\uD83D\uDCB5 Deposit"));
    private String balance = String.valueOf(RandomData.getBalance());
    private String invalidBalance = "10001";

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositPage selectAccountAndEnterAmount() {
        selectAccount.selectOption(1);
        enterAmount.setValue(balance);
        depositButton.click();
        return this;
    }

    public DepositPage selectAccountAndEnterExcessAmount() {
        selectAccount.selectOption(1);
        enterAmount.setValue(invalidBalance);
        depositButton.click();
        return this;
    }
}
