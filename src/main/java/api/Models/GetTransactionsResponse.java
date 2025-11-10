package api.Models;

import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class GetTransactionsResponse extends BaseModel {

    private List<TransactionsResponse> transactions;
}
