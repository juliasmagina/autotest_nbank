package Specs;

import Models.LoginUserRequest;
import Requests.skeleton.Endpoint;
import Requests.skeleton.requesters.CrudRequester;
import configs.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestSpecifications {

    private static final Map<String, String> tokens = new HashMap<>(Map.of("admin", "Basic YWRtaW46YWRtaW4="));

    private RequestSpecifications() {
    }


    private static RequestSpecBuilder defaultRequestSpecBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()))
                .setBaseUri(Config.getProperties("server") + Config.getProperties("apiVersion"));
    }

    public static RequestSpecification unauthSpec() {
        return defaultRequestSpecBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestSpecBuilder().addHeader("Authorization", tokens.get("admin")).build();
    }

    public static RequestSpecification userSpec(String username, String password) {
        String userBasicAuth;
        if (!tokens.containsKey(username)) {
            userBasicAuth = new CrudRequester(Endpoint.LOGIN,
                    RequestSpecifications.unauthSpec(), ResponseSpecifications.statusOk())
                    .post(LoginUserRequest.builder().username(username).password(password).build())
                    .extract()
                    .header("Authorization");

            tokens.put(username, userBasicAuth);
        } else {
            userBasicAuth = tokens.get(username);
        }

        return defaultRequestSpecBuilder().addHeader("Authorization", userBasicAuth)
                .build();
    }
}
