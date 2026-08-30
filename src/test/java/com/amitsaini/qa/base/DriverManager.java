package com.amitsaini.qa.base;

import org.openqa.selenium.WebDriver;

/**
 * Holds the WebDriver in a ThreadLocal.
 *
 * TestNG can run methods in parallel threads. A single static WebDriver would
 * be shared across those threads and the tests would fight over one browser.
 * A ThreadLocal gives each thread its own driver, which is what makes
 * parallel="methods" in testng.xml safe.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void setDriver(WebDriver driver) {
        DRIVER.set(driver);
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
        }
        // remove() matters: without it the thread keeps a reference to a dead
        // driver and leaks memory across a long suite.
        DRIVER.remove();
    }
}
