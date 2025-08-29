package iteration2;

import Generators.RandomData;
import Models.ChangeUserNameRequest;
import Models.ChangeUsernameResponse;
import Models.CreateUserRequest;
import Models.ViewProfileResponse;
import Requests.ChangeUsernameRequester;
import Requests.ViewProfileRequester;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;
import iteration2.Steps.CreateUserSteps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ChangeUsernameTest extends BaseTest {

    private String generatedUsername;
    private String generatedPassword;

    @BeforeEach
    public void setUp() {
        CreateUserRequest user = CreateUserSteps.createUser();
        this.generatedUsername = user.getUsername();
        this.generatedPassword = user.getPassword();
    }


    @Test
    @DisplayName("User can change his name for two words")
    public void userCanChangeHisNameTest() {

        ViewProfileResponse initialProfile = new ViewProfileRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get().extract().as(ViewProfileResponse.class);

        softly.assertThat(initialProfile.getName()).isEqualTo(null);

        ChangeUserNameRequest newNameRequest = ChangeUserNameRequest.builder().name(RandomData.getNewUsername()).build();

        ChangeUsernameResponse changeUsernameResponse = new ChangeUsernameRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword),
                ResponseSpecifications.statusOk())
                .put(newNameRequest).extract().as(ChangeUsernameResponse.class);

        softly.assertThat(newNameRequest.getName()).isEqualTo(changeUsernameResponse.getCustomer().getName());

        ViewProfileResponse updatedProfile = new ViewProfileRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get().extract().as(ViewProfileResponse.class);

        softly.assertThat(initialProfile.getName()).isNotEqualTo(updatedProfile.getName());
        softly.assertThat(updatedProfile.getName()).isEqualTo(changeUsernameResponse.getCustomer().getName());


    }


    // negative

    public static Stream<Arguments> InvalidNames() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of("Ira"),
                Arguments.of("Ira Ira Ira"),
                Arguments.of("Ira Ira123"),
                Arguments.of("Ira Ira$!"),
                Arguments.of("Ira  Ira")  // two words with double space
        );
    }

    @MethodSource("InvalidNames")
    @ParameterizedTest
    @DisplayName("User can not change for invalid name + check that name is not changed")
    public void userCanNotChangeForInvalidNameTest(String name) {
        ViewProfileResponse initialProfile = new ViewProfileRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get().extract().as(ViewProfileResponse.class);

        softly.assertThat(initialProfile.getName()).isEqualTo(null);

        ChangeUserNameRequest newNameRequest = ChangeUserNameRequest.builder().name(name).build();

        new ChangeUsernameRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword),
                ResponseSpecifications.returnsBadRequest("Name must contain two words with letters only"))
                .put(newNameRequest);


        ViewProfileResponse updatedProfile = new ViewProfileRequester(RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get().extract().as(ViewProfileResponse.class);

        softly.assertThat(updatedProfile.getName()).isEqualTo(initialProfile.getName());
    }

}
