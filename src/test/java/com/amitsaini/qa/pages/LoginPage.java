package com.amitsaini.qa.pages;

import com.amitsaini.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * https://www.saucedemo.com/
 */
public class LoginPage extends BasePage {

    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("h3[data-test='error']");
    private final By errorCloseButton = By.className("error-button");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    /**
     * Clicks Login and returns the inventory page. Only use this when the
     * credentials are expected to be valid.
     */
    public InventoryPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(loginButton);
        return new InventoryPage(driver);
    }

    /**
     * Clicks Login and stays on the login page. Use this for negative cases,
     * where returning an InventoryPage would be a lie.
     */
    public LoginPage loginExpectingFailure(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        click(loginButton);
        return this;
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    public LoginPage dismissError() {
        click(errorCloseButton);
        return this;
    }

    public boolean isLoginButtonDisplayed() {
        return isDisplayed(loginButton);
    }
}
