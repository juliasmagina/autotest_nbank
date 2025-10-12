package common.extensions;

import api.Steps.UserSteps;
import common.annotations.Deposit;
import common.storage.AccountStorage;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DepositExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Deposit annotation = extensionContext.getRequiredTestMethod().getAnnotation(Deposit.class);
        if (annotation != null) {
            int transactionQuantity = annotation.transactionQuantity();

            for (int i = 0; i < transactionQuantity; i++) {
                UserSteps.deposit(SessionStorage.getUser(annotation.userIndex()), AccountStorage.getAccount(annotation.accountIndex()));

            }
        }
    }
}
