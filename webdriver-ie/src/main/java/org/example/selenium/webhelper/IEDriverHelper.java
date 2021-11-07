package org.example.selenium.webhelper;

import java.io.File;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;

/**
 * Selenium WebDriver helper class for MS Internet Explorer browser.
 *
 * @author Mike Derban
 * @since 2021-11-06
 */
public final class IEDriverHelper extends WebDriverHelper {

  private static final String WEBDRIVER_PROPERTIES_FILENAME = "webdriver-ie.properties";

  /**
   * Default constructor, uses default webdriver properties file and the default webdriver download
   * directory.
   */
  @SuppressWarnings("unused")
  public IEDriverHelper() {
    super(WEBDRIVER_PROPERTIES_FILENAME, null);
  }

  /**
   * Initializes an instance of Selenium web driver using an external property file and the default
   * webdriver download directory.
   *
   * @param propsFileName full file name of the driver properties file
   */
  @SuppressWarnings("unused")
  public IEDriverHelper(String propsFileName) {
    super(propsFileName, null);
  }

  /**
   * Initializes an instance of Selenium web driver using the default webdriver properties file and
   * a custom webdriver download directory.
   *
   * @param downloadDir destination directory to download web driver executable
   */
  @SuppressWarnings("unused")
  public IEDriverHelper(File downloadDir) {
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
  public IEDriverHelper(String propsFileName, File downloadDir) {
    super(propsFileName, downloadDir);
  }

  @Override
  public void initialize() {
    MutableCapabilities capabilities = getWebDriverOptions().getCapabilities();
    InternetExplorerOptions internetExplorerOptions = new InternetExplorerOptions();

    // Transfer capabilities to InternetExplorerOptions
    getWebDriverOptions()
        .getCapabilities()
        .getCapabilityNames()
        .forEach(
            capName ->
                internetExplorerOptions.setCapability(
                    capName, capabilities.getCapability(capName)));

    internetExplorerOptions.setCapability(
        InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

    // Useful automated testing capabilities
    internetExplorerOptions.setCapability("elementScrollBehavior", true);
    internetExplorerOptions.setCapability("enableElementCacheCleanup", true);
    internetExplorerOptions.setCapability("handlesAlerts", true);
    internetExplorerOptions.setCapability("ie.validateCookieDocumentType", true);
    internetExplorerOptions.setCapability("ignoreProtectedModeSettings", true);
    internetExplorerOptions.setCapability("ignoreZoomSetting", true);
    internetExplorerOptions.setCapability("initialBrowserUrl", "");
    internetExplorerOptions.setCapability("javascriptEnabled", true);
    internetExplorerOptions.setCapability("nativeEvents", true);
    internetExplorerOptions.setCapability("requireWindowFocus", true);
    internetExplorerOptions.setCapability("takesScreenshot", true);
    internetExplorerOptions.setCapability("unexpectedAlertBehaviour", "ignore");

    setDriver(new InternetExplorerDriver(internetExplorerOptions));
    initWebDriver(getWebDriverOptions());
  }
}
