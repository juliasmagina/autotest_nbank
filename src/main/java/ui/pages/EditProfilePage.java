package ui.pages;

import api.Models.ChangeUserNameRequest;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditProfilePage extends BasePage<EditProfilePage> {
    private SelenideElement newNameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private SelenideElement saveButton = $(Selectors.byText("\uD83D\uDCBE Save Changes"));

    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditProfilePage enterNewName(ChangeUserNameRequest changeUserNameRequest) {
        newNameInput.setValue(changeUserNameRequest.getName());
        saveButton.click();
        return this;
    }
}
