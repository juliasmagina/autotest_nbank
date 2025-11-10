package api.Models;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TransferResponse extends BaseModel {
    private float amount;
    private String message;
    private long senderAccountId;
    private long receiverAccountId;
    private String description;

}
