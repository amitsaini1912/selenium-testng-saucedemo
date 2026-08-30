package com.amitsaini.qa.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures a PNG on failure. The file name carries the test name and a timestamp
 * so a CI run with several failures produces distinct, traceable evidence.
 */
public final class ScreenshotUtil {

    private static final Path SCREENSHOT_DIR = Paths.get("target", "screenshots");
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private ScreenshotUtil() {
    }

    /**
     * @return the path of the saved screenshot, or null if it could not be taken.
     */
    public static String capture(WebDriver driver, String testName) {
        if (driver == null) {
            return null;
        }
        try {
            Files.createDirectories(SCREENSHOT_DIR);
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String fileName = testName + "-" + LocalDateTime.now().format(STAMP) + ".png";
            Path target = SCREENSHOT_DIR.resolve(fileName);
            Files.write(target, png);
            return target.toAbsolutePath().toString();
        } catch (IOException | RuntimeException e) {
            System.err.println("Could not capture screenshot for " + testName + ": " + e.getMessage());
            return null;
        }
    }
}
