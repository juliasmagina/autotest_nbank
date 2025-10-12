package iteration2.ui;

import api.configs.Config;
import com.codeborne.selenide.Configuration;
import common.extensions.*;
import iteration2.api.BaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

@ExtendWith(AdminSessionExtension.class)
@ExtendWith(UserSessionExtension.class)
@ExtendWith(CreatingUserAccountExtension.class)
@ExtendWith(DepositExtension.class)
@ExtendWith(BrowserMatchExtension.class)
public class BaseUITest extends BaseTest {


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
