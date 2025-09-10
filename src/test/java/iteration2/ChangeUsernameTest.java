package iteration2;

import Generators.RandomModelGenerator;
import Models.ChangeUserNameRequest;
import Models.ChangeUsernameResponse;
import Models.CreateUserRequest;
import Models.ViewProfileResponse;
import Models.comparison.ModelAssertions;
import Requests.skeleton.Endpoint;
import Requests.skeleton.requesters.CrudRequester;
import Requests.skeleton.requesters.ValidatedCrudRequester;
import Specs.RequestSpecifications;
import Specs.ResponseSpecifications;
import iteration2.Steps.AdminSteps;
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
        CreateUserRequest user = AdminSteps.createUser();
        this.generatedUsername = user.getUsername();
        this.generatedPassword = user.getPassword();
    }


    @Test
    @DisplayName("User can change his name for two words")
    public void userCanChangeHisNameTest() {

        ViewProfileResponse initialProfile = new ValidatedCrudRequester<ViewProfileResponse>(Endpoint.VIEW_PROFILE,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get();

        softly.assertThat(initialProfile.getName()).isEqualTo(null);


        ChangeUserNameRequest newNameRequest = RandomModelGenerator.generate(ChangeUserNameRequest.class);

        ChangeUsernameResponse changeUsernameResponse = new ValidatedCrudRequester<ChangeUsernameResponse>(Endpoint.CHANGE_NAME,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword),
                ResponseSpecifications.statusOk())
                .update(newNameRequest);

        ModelAssertions.assertThatModels(newNameRequest, changeUsernameResponse.getCustomer()).match();

        ViewProfileResponse updatedProfile = new ValidatedCrudRequester<ViewProfileResponse>(Endpoint.VIEW_PROFILE,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get();

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
        ViewProfileResponse initialProfile = new ValidatedCrudRequester<ViewProfileResponse>(Endpoint.VIEW_PROFILE,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get();

        softly.assertThat(initialProfile.getName()).isEqualTo(null);

        ChangeUserNameRequest newNameRequest = ChangeUserNameRequest.builder().name(name).build();

        new CrudRequester(Endpoint.CHANGE_NAME,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword),
                ResponseSpecifications.returnsBadRequest("Name must contain two words with letters only"))
                .update(newNameRequest);


        ViewProfileResponse updatedProfile = new ValidatedCrudRequester<ViewProfileResponse>(Endpoint.VIEW_PROFILE,
                RequestSpecifications.userSpec(generatedUsername, generatedPassword), ResponseSpecifications.statusOk())
                .get();

        softly.assertThat(updatedProfile.getName()).isEqualTo(initialProfile.getName());
    }

}
