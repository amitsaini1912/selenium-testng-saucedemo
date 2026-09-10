package com.amitsaini.qa.pages;

import com.amitsaini.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Covers all three checkout steps: information, overview, and complete.
 * They are one page object because the user experiences them as one flow.
 */
public class CheckoutPage extends BasePage {

    // Step one: customer information
    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By cancelButton = By.id("cancel");
    private final By errorMessage = By.cssSelector("h3[data-test='error']");

    // Step two: overview
    private final By subtotalLabel = By.className("summary_subtotal_label");
    private final By taxLabel = By.className("summary_tax_label");
    private final By totalLabel = By.className("summary_total_label");
    private final By finishButton = By.id("finish");

    // Step three: confirmation
    private final By completeHeader = By.className("complete-header");
    private final By backHomeButton = By.id("back-to-products");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutPage enterCustomerInformation(String firstName, String lastName, String postalCode) {
        type(firstNameInput, firstName);
        type(lastNameInput, lastName);
        type(postalCodeInput, postalCode);
        return this;
    }

    public CheckoutPage clickContinue() {
        click(continueButton);
        return this;
    }

    public CheckoutPage clickFinish() {
        click(finishButton);
        return this;
    }

    public boolean isErrorDisplayed() {
        return isEventuallyVisible(errorMessage);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    /** Item subtotal, before tax. */
    public double getSubtotal() {
        return parseAmount(getText(subtotalLabel));
    }

    public double getTax() {
        return parseAmount(getText(taxLabel));
    }

    public double getTotal() {
        return parseAmount(getText(totalLabel));
    }

    /** Turns "Item total: $29.99" into 29.99 */
    private double parseAmount(String label) {
        return Double.parseDouble(label.substring(label.indexOf('$') + 1).trim());
    }

    public String getConfirmationMessage() {
        return getText(completeHeader);
    }

    public boolean isOrderComplete() {
        return isEventuallyVisible(completeHeader);
    }

    public InventoryPage backToProducts() {
        click(backHomeButton);
        return new InventoryPage(driver);
    }
}
