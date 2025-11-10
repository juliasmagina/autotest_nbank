package api.Models;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferResponseNew extends BaseModel {
    private String status;
    private String message;
    private Long transactionId;
    private Long senderAccountId;
    private Long receiverAccountId;
    private double amount;
    private double fraudRiskScore;
    private String fraudReason;
    private boolean requiresVerification;
    private boolean requiresManualReview;
}
