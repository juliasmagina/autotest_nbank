package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {
    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS("Username must be between 3 and 15 characters"),
    NEW_ACCOUNT_CREATED("✅ New Account Created! Account Number:"),
    SUCCESSFULLY_DEPOSIT("✅ Successfully deposited " + " to account"),
    PLEASE_DEPOSIT_LESS("❌ Please deposit less or equal to 5000$."),
    SUCCESSFULLY_TRANSFERRED("✅ Successfully transferred" + "to account" + "!"),
    INVALID_TRANSFER("❌ Error: Invalid transfer: insufficient funds or invalid accounts"),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    NAME_MUST_CONTAIN("Name must contain two words with letters only");

    private final String message;

    BankAlert(String message) {
        this.message = message;
    }
}
