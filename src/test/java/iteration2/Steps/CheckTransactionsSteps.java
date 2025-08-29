package iteration2.Steps;

import Models.CreateAccountResponse;
import Models.CreateUserRequest;
import Models.TransactionsResponse;
import Requests.CheckTransactionsRequester;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;

import java.util.List;

public class CheckTransactionsSteps {

    public static List<TransactionsResponse> checkTransactions(CreateUserRequest user, CreateAccountResponse account) {
        return new CheckTransactionsRequester(RequestSpecifications.userSpec(user.getUsername(), user.getPassword()), ResponseSpecifications.statusOk())
                .get(account.getId())
                .extract()
                .jsonPath()
                .getList("", TransactionsResponse.class);

    }
}
