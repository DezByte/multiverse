package org.multiverse.benchmarkframework;

import org.multiverse.benchmarkframework.executor.TestCase;

import java.util.Properties;

/**
 * The result of executing a {@link org.multiverse.benchmarkframework.executor.TestCase}.
 * <p/>
 * todo:
 * Properties should be replaced by a map. Properties is a leaky abstraction from the
 * {@link FileBasedBenchmarkResultRepository}.
 *
 * @author Peter Veentjer.
 */
public class TestCaseResult {

    private final Properties properties;

    /**
     * Creates a new TestResult for a specific testcase. This instance can
     * be used to add all kinds of properties and measurements.
     *
     * @param testCase the TestCase this TestResult is for.
     * @param attempt
     * @throws NullPointerException     if testCase is null.
     * @throws IllegalArgumentException if attempt smaller than 1.
     */
    public TestCaseResult(TestCase testCase, int attempt) {
        if (testCase == null) {
            throw new NullPointerException();
        }

        if (attempt < 1) {
            throw new IllegalArgumentException();
        }

        properties = new Properties();
        copyPropertiesFromTestCase(testCase);
        put("benchmarkName", testCase.getBenchmarkName());
        put("attempt", attempt);
    }

    public TestCaseResult(Properties properties) {
        if (properties == null) {
            throw new NullPointerException();
        }
        this.properties = properties;
    }

    private void copyPropertiesFromTestCase(TestCase testCase) {
        Properties testCaseProps = testCase.getProperties();
        for (String name : testCaseProps.stringPropertyNames()) {
            String value = testCaseProps.getProperty(name);
            properties.put(name, value);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public void put(String key, Object value) {
        properties.put(key, value.toString());
    }

    private String getExistingProperty(String name) {
        String value = properties.getProperty(name);

        if (value == null) {
            throw new IllegalArgumentException("property with name " + name + " is not found");
        }
        return value;
    }

    public int getIntProperty(String name) {
        String value = getExistingProperty(name);
        return Integer.parseInt(value);
    }

    public long getLongProperty(String name) {
        String value = getExistingProperty(name);

        return Long.parseLong(value);
    }

    public String getBenchmarkName() {
        return getExistingProperty("benchmarkName");
    }

    public int getAttempt() {
        return getIntProperty("attempt");
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return properties.toString();
    }
}