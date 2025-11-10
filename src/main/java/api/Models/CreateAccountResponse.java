package api.Models;

import lombok.*;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CreateAccountResponse extends BaseModel {

    private long id;
    private String accountNumber;
    private float balance;
    private List<TransactionsResponse> transactions;

}
