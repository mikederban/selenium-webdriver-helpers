package org.example.selenium.webhelper;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.LoggerFactory;

/**
 * Selenium WebDriver helper class for Chrome browser.
 *
 * @author Mike Derban
 * @since 2021-11-06
 */
public final class ChromeDriverHelper extends WebDriverHelper {

  private static final String WEBDRIVER_PROPERTIES_FILENAME = "webdriver-chrome.properties";

  /**
   * Default constructor, uses default webdriver properties file and default webdriver download
   * directory.
   */
  @SuppressWarnings("unused")
  public ChromeDriverHelper() {
    super(WEBDRIVER_PROPERTIES_FILENAME, null);
  }

  /**
   * Initializes an instance of Selenium web driver using an external property file and default
   * webdriver download directory.
   *
   * @param propsFileName full file name of the driver properties file
   */
  @SuppressWarnings("unused")
  public ChromeDriverHelper(String propsFileName) {
    super(propsFileName, null);
  }

  /**
   * Initializes an instance of Selenium web driver using the default webdriver properties file and
   * a custom webdriver download directory.
   *
   * @param downloadDir destination directory to download web driver executable
   */
  @SuppressWarnings("unused")
  public ChromeDriverHelper(File downloadDir) {
    super(WEBDRIVER_PROPERTIES_FILENAME, downloadDir);
  }

  /**
   * Initializes an instance of Selenium web driver using an external property file and custom
   * driver download location.
   *
   * @param propsFileName full file name of the driver properties file
   * @param downloadDir destination directory to download web driver executable
   */
  @SuppressWarnings("unused")
  public ChromeDriverHelper(String propsFileName, File downloadDir) {
    super(propsFileName, downloadDir);
  }

  @Override
  public void initialize() {
    MutableCapabilities capabilities = getWebDriverOptions().getCapabilities();
    ChromeOptions chromeOptions = new ChromeOptions();

    // Transfer capabilities to ChromeOptions
    getWebDriverOptions()
        .getCapabilities()
        .getCapabilityNames()
        .forEach(
            capName -> chromeOptions.setCapability(capName, capabilities.getCapability(capName)));

    // Add Chrome specific ChromeOptions
    Arrays.asList(
            Arrays.stream(getWebDriverOptions().getProperty("browser.options").split(","))
                .map(String::trim)
                .toArray(String[]::new))
        .forEach(chromeOptions::addArguments);

    // Disable web driver logging
    System.setProperty(ChromeDriverService.CHROME_DRIVER_VERBOSE_LOG_PROPERTY, "false");
    System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
    Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);

    // Create an instance of the service to tell chromedriver to use any available port
    ChromeDriverService service = new ChromeDriverService.Builder().usingAnyFreePort().build();

    // Create and instantiate web driver
    try {
      setDriver(new ChromeDriver(service, chromeOptions));
      initWebDriver(getWebDriverOptions());
    } catch (SessionNotCreatedException e) {
      LoggerFactory.getLogger(getClass()).error("Chrome driver was not initialized!", e);
    }
  }
}
