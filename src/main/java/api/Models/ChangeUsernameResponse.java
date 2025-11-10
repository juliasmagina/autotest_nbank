package api.Models;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class ChangeUsernameResponse extends BaseModel {

    private String message;
    private ViewProfileResponse customer;

}
