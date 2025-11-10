package common.extensions;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import common.annotations.FraudCheckMock;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class FraudCheckWireMockExtension implements BeforeEachCallback, AfterEachCallback {

    private WireMockServer wireMockServer;

    @Override
    public void beforeEach(ExtensionContext context) {
        FraudCheckMock mockConfig = context.getTestMethod()
                .map(method -> method.getAnnotation(FraudCheckMock.class))
                .orElseGet(() -> context.getTestClass()
                        .map(clazz -> clazz.getAnnotation(FraudCheckMock.class))
                        .orElse(null));

        if (mockConfig != null) {
            setupWireMockStubs(mockConfig);
        }
    }

    private void setupWireMockStubs(FraudCheckMock config) {
        System.out.println("🎯 ========== CONFIGURING EXISTING WIREMOCK ==========");

        // Configure client to point to the Docker WireMock (port 8080)
        WireMock.configureFor("localhost", 8080);

        // First, remove any existing mappings for our endpoint to avoid conflicts
        try {
            // List all stubs and remove ones matching our endpoint
            WireMock.reset();
            System.out.println("✅ RESET ALL WIREMOCK STUBS");
        } catch (Exception e) {
            System.out.println("⚠️ Could not reset WireMock: " + e.getMessage());
        }

        // Configure the fraud check endpoint
        String responseBody = String.format("{\n" +
                        "  \"status\": \"%s\",\n" +
                        "  \"decision\": \"%s\",\n" +
                        "  \"riskScore\": %s,\n" +
                        "  \"reason\": \"%s\",\n" +
                        "  \"requiresManualReview\": %s,\n" +
                        "  \"additionalVerificationRequired\": %s\n" +
                        "}",
                config.status(),
                config.decision(),
                String.valueOf(config.riskScore()).replace(",", "."),
                config.reason(),
                config.requiresManualReview(),
                config.additionalVerificationRequired());

        System.out.println("📨 CONFIGURING FRAUD CHECK RESPONSE: " + responseBody);

        // Create the stub
        stubFor(post(urlPathEqualTo("/fraud-check"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        System.out.println("✅ FRAUD CHECK ENDPOINT CONFIGURED: POST /fraud-check");

        // Verify the stub was created by testing it
        try {
            System.out.println("🔄 Verifying WireMock configuration...");
            String testResponse = java.net.http.HttpClient.newHttpClient().send(
                    java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create("http://localhost:8080/fraud-check"))
                            .header("Content-Type", "application/json")
                            .POST(java.net.http.HttpRequest.BodyPublishers.ofString("{\"test\": \"data\"}"))
                            .build(),
                    java.net.http.HttpResponse.BodyHandlers.ofString()
            ).body();
            System.out.println("✅ WIREMOCK VERIFICATION SUCCESS: " + testResponse);
        } catch (Exception e) {
            System.out.println("❌ WIREMOCK VERIFICATION FAILED: " + e.getMessage());
        }

        System.out.println("🎯 ========== WIREMOCK READY ==========");
    }

    @Override
    public void afterEach(ExtensionContext context) {
        // Optional: Clean up after test
        try {
            WireMock.configureFor("localhost", 8080);
            WireMock.reset();
            System.out.println("🧹 CLEANED UP WIREMOCK STUBS");
        } catch (Exception e) {
            System.out.println("⚠️ Could not clean up WireMock: " + e.getMessage());
        }
    }
}