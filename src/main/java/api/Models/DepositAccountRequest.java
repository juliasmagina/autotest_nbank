package api.Models;


import api.Generators.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositAccountRequest extends BaseModel {

    private long id;
    @GeneratingRule(regex = "\"^([1-9]\\\\d{0,2}|[1-4]\\\\d{3}|5000)$\"")
    private float balance;
}
