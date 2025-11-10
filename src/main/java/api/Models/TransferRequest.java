package api.Models;

import api.Generators.GeneratingRule;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransferRequest extends BaseModel {

    private long senderAccountId;
    private long receiverAccountId;
    @GeneratingRule(regex = "^([1-9][0-9]{0,3})$")
    private float amount;
    private String description;
}
