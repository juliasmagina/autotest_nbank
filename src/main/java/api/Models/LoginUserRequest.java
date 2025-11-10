package api.Models;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class LoginUserRequest extends BaseModel {

    private String username;
    private String password;
}
