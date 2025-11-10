package api.Models;

import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ViewProfileResponse extends BaseModel {

    private long id;
    private String username;
    private String password;
    private String name;
    private ROLES role;
    private List<CreateAccountResponse> accounts;
}
