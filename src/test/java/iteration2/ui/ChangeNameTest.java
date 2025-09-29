package iteration2.ui;

import Generators.RandomModelGenerator;
import Models.ChangeUserNameRequest;
import Models.CreateUserRequest;
import Models.LoginUserRequest;
import Models.ViewProfileResponse;
import Requests.skeleton.Endpoint;
import Requests.skeleton.requesters.CrudRequester;
import Requests.skeleton.requesters.ValidatedCrudRequester;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;
import Steps.AdminSteps;
import com.codeborne.selenide.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import java.util.Map;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;


public class ChangeNameTest {

    @BeforeAll
    public static void setUpSelenide() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://host.docker.internal:3000";
        Configuration.browserSize = "1920x1080";
        Configuration.browser = "chrome";

        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
    }

    @Test
    public void userCanChangeHisNameTest() {

        CreateUserRequest user = AdminSteps.createUser();
        ViewProfileResponse initialProfile = new ValidatedCrudRequester<ViewProfileResponse>(Endpoint.VIEW_PROFILE, RequestSpecifications.userSpec(user.getUsername(), user.getPassword()), ResponseSpecifications.statusOk()).get();

        String userBasicAuth = new CrudRequester(Endpoint.LOGIN, RequestSpecifications.unauthSpec(), ResponseSpecifications.statusOk()).post(LoginUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build()).extract().header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userBasicAuth);

        ChangeUserNameRequest newNameRequest = RandomModelGenerator.generate(ChangeUserNameRequest.class);

        Selenide.open("/dashboard");

        SelenideElement nonameElement = $x("//span[@class='user-name' and text()='Noname']").shouldBe(Condition.exist);
        executeJavaScript("arguments[0].click();", nonameElement);
        Selenide.sleep(1000);
        $(Selectors.byAttribute("placeholder", "Enter new name")).setValue(newNameRequest.getName());
        $(Selectors.byText("\uD83D\uDCBE Save Changes")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText.contains("✅ Name updated successfully!"));
        alert.accept();

        Selenide.refresh();
        Selenide.open("/dashboard");

        $("h2.welcome-text").shouldHave(Condition.text("Welcome, " + newNameRequest.getName() + "!"));

        ViewProfileResponse updatedProfile = new ValidatedCrudRequester<ViewProfileResponse>(Endpoint.VIEW_PROFILE, RequestSpecifications.userSpec(user.getUsername(), user.getPassword()), ResponseSpecifications.statusOk()).get();

        assertThat(initialProfile.getName()).isNotEqualTo(updatedProfile.getName());
        assertThat(updatedProfile.getName()).isEqualTo(newNameRequest.getName());
    }


    @Test
    public void userCanNotChangeHisNameForInvalidTest() {

        CreateUserRequest user = AdminSteps.createUser();
        ViewProfileResponse initialProfile = new ValidatedCrudRequester<ViewProfileResponse>(Endpoint.VIEW_PROFILE, RequestSpecifications.userSpec(user.getUsername(), user.getPassword()), ResponseSpecifications.statusOk()).get();

        String userBasicAuth = new CrudRequester(Endpoint.LOGIN, RequestSpecifications.unauthSpec(), ResponseSpecifications.statusOk()).post(LoginUserRequest.builder().username(user.getUsername()).password(user.getPassword()).build()).extract().header("Authorization");

        Selenide.open("/");
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userBasicAuth);

        ChangeUserNameRequest newNameRequest = RandomModelGenerator.generate(ChangeUserNameRequest.class);
        newNameRequest.setName("Ira");

        Selenide.open("/dashboard");

        SelenideElement nonameElement = $x("//span[@class='user-name' and text()='Noname']").shouldBe(Condition.exist);
        executeJavaScript("arguments[0].click();", nonameElement);
        Selenide.sleep(1000);
        $(Selectors.byAttribute("placeholder", "Enter new name")).setValue(newNameRequest.getName());
        $(Selectors.byText("\uD83D\uDCBE Save Changes")).click();

        Alert alert = switchTo().alert();
        String alertText = alert.getText();
        assertThat(alertText.contains("Name must contain two words with letters only"));
        alert.accept();

        Selenide.refresh();
        Selenide.open("/dashboard");

        $("h2.welcome-text").shouldHave(Condition.text("Welcome, noname!"));

        ViewProfileResponse updatedProfile = new ValidatedCrudRequester<ViewProfileResponse>(Endpoint.VIEW_PROFILE, RequestSpecifications.userSpec(user.getUsername(), user.getPassword()), ResponseSpecifications.statusOk()).get();

        assertThat(initialProfile.getName()).isEqualTo(updatedProfile.getName());
        assertThat(updatedProfile.getName()).isNull();
    }
}
