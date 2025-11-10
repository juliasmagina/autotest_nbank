package common.extensions;

import api.Models.CreateUserRequest;
import api.Steps.AdminSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.LinkedList;
import java.util.List;

import static api.Specs.RequestSpecifications.getUserAuthHeader;

public class UserSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        UserSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class);

        if (annotation != null) {
            int userCount = annotation.value();

            SessionStorage.clear();

            List<CreateUserRequest> users = new LinkedList<>();
            for (int i = 0; i < userCount; i++) {
                CreateUserRequest userRequest = AdminSteps.createUser();
                users.add(userRequest);
            }
            SessionStorage.addUsers(users);

            int authAsUser = annotation.auth();
            CreateUserRequest userToAuth = SessionStorage.getUser(authAsUser);
            String authHeader = getUserAuthHeader(
                    userToAuth.getUsername(),
                    userToAuth.getPassword()
            );

            // Сохраняем auth header в SessionStorage для использования в тестах
            SessionStorage.setAuthHeader(authHeader);
        }
    }
}
