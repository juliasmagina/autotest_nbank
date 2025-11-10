package iteration2.api;


import api.Generators.RandomData;
import api.Models.TransferResponseNew;
import api.Models.comparison.ModelAssertions;
import api.Steps.UserSteps;
import common.annotations.CreatingUserAccount;
import common.annotations.Deposit;
import common.annotations.FraudCheckMock;
import common.annotations.UserSession;
import common.extensions.FraudCheckWireMockExtension;
import common.extensions.TimingExtension;
import common.storage.AccountStorage;
import common.storage.SessionStorage;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({TimingExtension.class, FraudCheckWireMockExtension.class})
public class TransferWithFraudCheckTest extends BaseTest {


    @BeforeEach
    public void setupTest() {
        this.softly = new SoftAssertions();
    }

    @Test
    @UserSession(value = 2)
    @CreatingUserAccount(value = 2)
    @Deposit
    @FraudCheckMock(
            status = "SUCCESS",
            decision = "APPROVED",
            riskScore = 0.2,
            reason = "Low risk transaction",
            requiresManualReview = false,
            additionalVerificationRequired = false
    )


    public void testTransferWithFraudCheck() {


        float transferAmount = RandomData.getAmount();

        TransferResponseNew transferResponse = UserSteps.transferWithFraudCheck(
                AccountStorage.getAccount(1).getId(),
                AccountStorage.getAccount(2).getId(),
                transferAmount,
                SessionStorage.getUser(1)
        );


        TransferResponseNew expectedResponse = TransferResponseNew.builder()
                .status("APPROVED")
                .message("Transfer approved and processed immediately")
                .amount(transferAmount)
                .senderAccountId(AccountStorage.getAccount(1).getId())
                .receiverAccountId(AccountStorage.getAccount(2).getId())
                .fraudRiskScore(0.2)
                .fraudReason("Low risk transaction")
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @AfterEach
    public void afterTest() {
        softly.assertAll();
    }
}