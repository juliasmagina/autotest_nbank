package api.Models;


import api.Generators.GeneratingRule;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class DepositAccountRequest extends BaseModel {

    private long accountId;
    @GeneratingRule(regex = "\"^([1-9]\\\\d{0,2}|[1-4]\\\\d{3}|5000)$\"")
    private float amount;
    private String description;

}
