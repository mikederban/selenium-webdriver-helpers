package org.example.selenium.webhelper;

import java.io.File;
import lombok.experimental.Helper;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

/**
 * Selenium WebDriver helper class for Edge browser.
 *
 * @author Mike Derban
 * @since 2021-11-06
 */
public final class EdgeDriverHelper extends WebDriverHelper {

  private static final String WEBDRIVER_PROPERTIES_FILENAME = "webdriver-edge.properties";

  /**
   * Default constructor, uses default webdriver properties file and default webdriver download
   * directory.
   */
  @SuppressWarnings("unused")
  public EdgeDriverHelper() {
    super(WEBDRIVER_PROPERTIES_FILENAME, null);
  }

  /**
   * Initializes an instance of Selenium web driver using an external property file and default
   * webdriver download directory.
   *
   * @param propsFileName full file name of the driver properties file
   */
  @SuppressWarnings("unused")
  public EdgeDriverHelper(String propsFileName) {
    super(propsFileName, null);
  }

  /**
   * Initializes an instance of Selenium web driver using the default webdriver properties file and
   * a custom webdriver download directory.
   *
   * @param downloadDir destination directory to download web driver executable
   */
  @SuppressWarnings("unused")
  public EdgeDriverHelper(File downloadDir) {
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
  public EdgeDriverHelper(String propsFileName, File downloadDir) {
    super(propsFileName, downloadDir);
  }

  @Override
  public void initialize() {
    MutableCapabilities capabilities = getWebDriverOptions().getCapabilities();
    EdgeOptions edgeOptions = new EdgeOptions();

    // Transfer capabilities to EdgeOptions
    getWebDriverOptions()
        .getCapabilities()
        .getCapabilityNames()
        .forEach(
            capName -> edgeOptions.setCapability(capName, capabilities.getCapability(capName)));

    setDriver(new EdgeDriver(edgeOptions));
    initWebDriver(getWebDriverOptions());
  }
}
