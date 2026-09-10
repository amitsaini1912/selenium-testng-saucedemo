package com.amitsaini.qa.base;

import com.amitsaini.qa.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Builds a WebDriver for the requested browser.
 *
 * Selenium 4.6+ ships Selenium Manager, which downloads the matching driver
 * binary automatically, so there is no WebDriverManager dependency and no
 * chromedriver checked into the repo.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver create() {
        String browser = ConfigReader.get("browser").toLowerCase().trim();
        boolean headless = ConfigReader.getBoolean("headless");

        WebDriver driver = switch (browser) {
            case "chrome" -> new ChromeDriver(chromeOptions(headless));
            case "firefox" -> new FirefoxDriver(firefoxOptions(headless));
            case "edge" -> new EdgeDriver(edgeOptions(headless));
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };

        // Implicit wait is deliberately NOT set. Mixing implicit and explicit
        // waits produces unpredictable timeouts; all waiting is explicit.
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getInt("page.load.timeout")));

        // Only maximize when a real window manager is present. In headless mode
        // (CI) maximize() collapses the viewport to Chrome's 800x600 default,
        // which overrides the --window-size argument and pushes buttons at the
        // bottom of the page out of view, causing lost clicks. The headless
        // options already fix the size at 1920x1080, so leave the window alone.
        if (!headless) {
            driver.manage().window().maximize();
        }
        return driver;
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        // Suppresses the "Chrome is being controlled by automated software" infobar
        options.addArguments("--disable-infobars");
        options.addArguments("--remote-allow-origins=*");
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        options.addArguments("--width=1920", "--height=1080");
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1920,1080");
        return options;
    }
}
