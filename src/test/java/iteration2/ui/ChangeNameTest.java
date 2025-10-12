package iteration2.ui;

import api.Generators.RandomModelGenerator;
import api.Models.ChangeUserNameRequest;
import api.Models.ViewProfileResponse;
import api.Steps.UserSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.EditProfilePage;
import ui.pages.UserDashboard;

import static org.assertj.core.api.Assertions.assertThat;


public class ChangeNameTest extends BaseUITest {

    @Test
    @UserSession
    public void userCanChangeHisNameTest() {

        ViewProfileResponse initialProfile = UserSteps.viewProfile(SessionStorage.getUser());

        ChangeUserNameRequest newNameRequest = RandomModelGenerator.generate(ChangeUserNameRequest.class);

        new UserDashboard().open().changeName().getPage(EditProfilePage.class).enterNewName(newNameRequest).checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage())
                .getPage(UserDashboard.class).open().checkNewName(newNameRequest);

        ViewProfileResponse updatedProfile = UserSteps.viewProfile(SessionStorage.getUser());

        assertThat(initialProfile.getName()).isNotEqualTo(updatedProfile.getName());
        assertThat(updatedProfile.getName()).isEqualTo(newNameRequest.getName());
    }


    @Test
    @UserSession
    public void userCanNotChangeHisNameForInvalidTest() {

        ViewProfileResponse initialProfile = UserSteps.viewProfile(SessionStorage.getUser());

        ChangeUserNameRequest newNameRequest = RandomModelGenerator.generate(ChangeUserNameRequest.class);
        newNameRequest.setName("Ira");

        new UserDashboard().open().changeName().getPage(EditProfilePage.class).enterNewName(newNameRequest).checkAlertMessageAndAccept(BankAlert.NAME_MUST_CONTAIN.getMessage())
                .getPage(UserDashboard.class).open().checkNewNameDoesNotChanged();

        ViewProfileResponse updatedProfile = UserSteps.viewProfile(SessionStorage.getUser());

        assertThat(initialProfile.getName()).isEqualTo(updatedProfile.getName());
        assertThat(updatedProfile.getName()).isNull();
    }
}
