package ui.pages;

import api.Generators.RandomData;
import api.Models.CreateAccountResponse;
import api.Models.CreateUserRequest;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class TransferPage extends BasePage<TransferPage> {
    private SelenideElement selectYourAccount = $(".account-selector");
    private SelenideElement recipientName = $(Selectors.byAttribute("placeholder", "Enter recipient name"));
    private SelenideElement recipientAccountNumber = $(Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private SelenideElement amountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement checkbox = $("#confirmCheck");
    private SelenideElement sendTranfer = $(Selectors.byText("\uD83D\uDE80 Send Transfer"));
    private String invalidAmount = "10001";

    public TransferPage sendTransfer(CreateUserRequest user, CreateAccountResponse accountResponse) {
        selectYourAccount.selectOption(1);
        recipientName.sendKeys(user.getUsername());
        recipientAccountNumber.setValue(accountResponse.getAccountNumber());
        String amount = String.valueOf(RandomData.getAmount());
        amountInput.setValue(amount);
        checkbox.setSelected(true);
        sendTranfer.click();
        return this;
    }

    public TransferPage sendTransferInvalidAmount(CreateUserRequest user, CreateAccountResponse accountResponse) {
        selectYourAccount.selectOption(1);
        recipientName.sendKeys(user.getUsername());
        recipientAccountNumber.setValue(accountResponse.getAccountNumber());
        amountInput.setValue(invalidAmount);
        checkbox.setSelected(true);
        sendTranfer.click();
        return this;
    }

    @Override
    public String url() {
        return "/transfer";
    }
}
