package Models;

import Generators.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransferRequest extends BaseModel {

    private long senderAccountId;
    private long receiverAccountId;
    @GeneratingRule(regex = "^([1-9]\\d{0,3}|10000)$")
    private float amount;
}
