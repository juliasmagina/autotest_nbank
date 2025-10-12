package common.storage;

import api.Models.CreateUserRequest;
import api.Steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SessionStorage {

    private static final SessionStorage INSTANCE = new SessionStorage();

    private final LinkedHashMap<CreateUserRequest, UserSteps> userStepsmap = new LinkedHashMap<>();

    private SessionStorage() {
    }

    public static void addUsers(List<CreateUserRequest> users) {
        for (CreateUserRequest user : users) {
            INSTANCE.userStepsmap.put(user, new UserSteps(user));
        }
    }

    public static CreateUserRequest getUser(int number) {
        return new ArrayList<>(INSTANCE.userStepsmap.keySet()).get(number - 1);
    }

    public static CreateUserRequest getUser() {
        return getUser(1);
    }

    public static UserSteps getStep(int number) {
        return new ArrayList<>(INSTANCE.userStepsmap.values()).get(number);
    }

    public static UserSteps getStep() {
        return getStep(1);
    }

    public static void clear() {
        INSTANCE.userStepsmap.clear();
    }


}
