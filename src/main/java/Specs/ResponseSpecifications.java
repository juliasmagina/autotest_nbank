package Specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class ResponseSpecifications {

    private ResponseSpecifications() {

    }

    ;

    private static ResponseSpecBuilder defaultResponseSpecificationsBuilder() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification entityWasCreated() {
        return defaultResponseSpecificationsBuilder().expectStatusCode(HttpStatus.SC_CREATED).build();
    }

    public static ResponseSpecification statusOk() {
        return defaultResponseSpecificationsBuilder().expectStatusCode(HttpStatus.SC_OK).build();
    }

    public static ResponseSpecification returnsBadRequest(String error) {
        return defaultResponseSpecificationsBuilder().expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(Matchers.containsString(error)).build();
    }

    public static ResponseSpecification returnsForbidden(String error) {
        return defaultResponseSpecificationsBuilder().expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody(Matchers.containsString(error)).build();
    }

}
