package api.Models;

import api.Generators.GeneratingRule;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ChangeUserNameRequest extends BaseModel {

    @GeneratingRule(regex = "[A-Za-z]{1,20} [A-Za-z]{1,20}")
    private String name;
}
