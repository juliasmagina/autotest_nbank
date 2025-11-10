package common.storage;

import api.Models.CreateUserRequest;
import api.Steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SessionStorage {

    private static final ThreadLocal<SessionStorage> INSTANCE = ThreadLocal.withInitial(SessionStorage::new);

    private final LinkedHashMap<CreateUserRequest, UserSteps> userStepsmap = new LinkedHashMap<>();
    private static String authHeader;

    private SessionStorage() {
    }

    public static void setAuthHeader(String header) {
        authHeader = header;
    }

    public static void addUsers(List<CreateUserRequest> users) {
        for (CreateUserRequest user : users) {
            INSTANCE.get().userStepsmap.put(user, new UserSteps(user));
        }
    }

    public static CreateUserRequest getUser(int number) {
        return new ArrayList<>(INSTANCE.get().userStepsmap.keySet()).get(number - 1);
    }

    public static CreateUserRequest getUser() {
        return getUser(1);
    }

    public static UserSteps getStep(int number) {
        return new ArrayList<>(INSTANCE.get().userStepsmap.values()).get(number);
    }

    public static UserSteps getStep() {
        return getStep(1);
    }

    public static void clear() {
        INSTANCE.get().userStepsmap.clear();
    }


}
