package Requests.skeleton;

import Models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Endpoint {

    ADMIN_USER(
            "/admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),

    ACCOUNT(
            "/accounts",
            BaseModel.class,
            CreateAccountResponse.class
    ),

    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),

    CHANGE_NAME(
            "/customer/profile",
            ChangeUserNameRequest.class,
            ChangeUsernameResponse.class
    ),

    CHECK_ACCOUNT(
            "/customer/accounts",
            BaseModel.class,
            CreateAccountResponse.class
    ),

    CHECK_TRANSACTIONS(
            "/accounts/{accountId}/transactions",
            BaseModel.class,
            TransactionsResponse.class
    ),

    DEPOSIT(
            "/accounts/deposit",
            DepositAccountRequest.class,
            DepositAccountResponse.class
    ),

    TRANSFER(
            "/accounts/transfer",
            TransferRequest.class,
            TransferResponse.class
    ),

    VIEW_PROFILE(
            "/customer/profile",
            BaseModel.class,
            ViewProfileResponse.class
    );


    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

    public String buildUrl(long accountId) {
        if (this == CHECK_TRANSACTIONS) {
            return url.replace("{accountId}", String.valueOf(accountId));
        }
        return url;
    }
}
