package iteration2.ui;

import api.Models.CreateUserRequest;
import api.Specs.RequestSpecifications;
import api.configs.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import iteration2.api.BaseTest;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class BaseUITest extends BaseTest {

    public void authAsUser(String username, String password) {
        Selenide.open("/");
        String userBasicAuth = RequestSpecifications.getUserAuthHeader(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userBasicAuth);
    }

    public void authAsUser(CreateUserRequest createUserRequest) {
        authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }

    @BeforeAll
    public static void setUpSelenide() {
        Configuration.remote = Config.getProperties("uiRemote");
        Configuration.baseUrl = Config.getProperties("uiBaseUrl");
        Configuration.browserSize = Config.getProperties("uiBrowserSize");
        Configuration.browser = Config.getProperties("uiBrowser");

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));
    }
}
