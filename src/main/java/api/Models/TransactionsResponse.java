package api.Models;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransactionsResponse extends BaseModel {

    private int id;
    private float amount;
    private TYPES type;
    private String timestamp;
    private long relatedAccountId;
    private CreateAccountResponse relatedAccount;
    private String status;
    private Boolean fraudCheckRequired;
    private String timestampAsString;
    private double amountAsDouble;

}
