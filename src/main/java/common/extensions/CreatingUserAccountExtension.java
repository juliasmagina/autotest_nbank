package common.extensions;

import api.Models.CreateAccountResponse;
import api.Steps.UserSteps;
import common.annotations.CreatingUserAccount;
import common.storage.AccountStorage;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.LinkedList;
import java.util.List;

public class CreatingUserAccountExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        CreatingUserAccount annotation = extensionContext.getRequiredTestMethod().getAnnotation(CreatingUserAccount.class);

        if (annotation != null) {
            int accountCount = annotation.value();
            AccountStorage.clear();

            List<CreateAccountResponse> accounts = new LinkedList<>();
            for (int i = 0; i < accountCount; i++) {
                CreateAccountResponse accountResponse = UserSteps.createAccount(SessionStorage.getUser(i + 1));
                accounts.add(accountResponse);
            }
            AccountStorage.addAccount(accounts);

        }
    }
}
