package Specs;

import Models.LoginUserRequest;
import Requests.LoginUserRequester;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public class RequestSpecifications {

    private RequestSpecifications() {
    }


    private static RequestSpecBuilder defaultRequestSpecBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()))
                .setBaseUri("http://localhost:4111");
    }

    public static RequestSpecification unauthSpec() {
        return defaultRequestSpecBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestSpecBuilder().addHeader("Authorization", "Basic YWRtaW46YWRtaW4=").build();
    }

    public static RequestSpecification userSpec(String username, String password) {
        String userBasicAuth = new LoginUserRequester(RequestSpecifications.unauthSpec(), ResponseSpecifications.statusOk())
                .post(LoginUserRequest.builder().username(username).password(password).build())
                .extract()
                .header("Authorization");

        return defaultRequestSpecBuilder().addHeader("Authorization",userBasicAuth )
                .build();
    }
}
