package Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransactionsResponse extends BaseModel {

    private int id;
    private float amount;
    private TYPES type;
    private String timestamp;
    private long relatedAccountId;
}
