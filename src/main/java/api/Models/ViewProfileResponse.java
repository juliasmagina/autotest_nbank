package api.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
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
