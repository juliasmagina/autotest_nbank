package common.storage;

import api.Models.CreateAccountResponse;

import java.util.LinkedList;
import java.util.List;

public class AccountStorage {

    private static final AccountStorage INSTANCE = new AccountStorage();

    private final LinkedList<CreateAccountResponse> accountsList = new LinkedList<>();

    private AccountStorage() {
    }

    public static void addAccount(List<CreateAccountResponse> accounts) {
        for (CreateAccountResponse account : accounts) {
            INSTANCE.accountsList.add(account);
        }
    }

    public static CreateAccountResponse getAccount(int number) {
        return INSTANCE.accountsList.get(number - 1);
    }

    public static CreateAccountResponse getAccount() {
        return getAccount(1);
    }

    public static void clear() {
        INSTANCE.accountsList.clear();
    }

}
