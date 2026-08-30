package com.amitsaini.qa.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Reads a CSV from src/test/resources and returns it in the Object[][] shape
 * that a TestNG @DataProvider expects.
 *
 * The first row is treated as a header and skipped.
 */
public final class CsvDataReader {

    private CsvDataReader() {
    }

    public static Object[][] read(String resourcePath) {
        try (InputStream in = CsvDataReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("CSV not found on the classpath: " + resourcePath);
            }
            try (CSVReader reader = new CSVReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                List<String[]> rows = reader.readAll();
                if (rows.size() <= 1) {
                    throw new IllegalStateException("CSV has no data rows: " + resourcePath);
                }
                List<String[]> dataRows = rows.subList(1, rows.size());

                Object[][] data = new Object[dataRows.size()][];
                for (int i = 0; i < dataRows.size(); i++) {
                    data[i] = dataRows.get(i);
                }
                return data;
            }
        } catch (IOException | CsvException e) {
            throw new IllegalStateException("Failed to read CSV: " + resourcePath, e);
        }
    }
}
