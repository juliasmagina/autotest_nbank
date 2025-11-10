package api.Models;


import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class DepositAccountResponse<T extends BaseModel> extends BaseModel {

    private long id;
    private String accountNumber;
    private float balance;
    private float depositAmount;
    private long transactionId;
}
