package Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransferResponse {
    private float amount;
    private String message;
    private long senderAccountId;
    private long receiverAccountId;

}
