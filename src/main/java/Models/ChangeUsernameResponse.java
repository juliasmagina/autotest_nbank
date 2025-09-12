package Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder


public class ChangeUsernameResponse extends BaseModel {

    private String message;
    private ViewProfileResponse customer;

}
