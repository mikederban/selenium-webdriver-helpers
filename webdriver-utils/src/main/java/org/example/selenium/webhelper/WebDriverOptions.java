package org.example.selenium.webhelper;

import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.CapabilityType;

/**
 * WebDriver Options class.
 *
 * @author Mike Derban
 * @since 2021-11-06
 */
public final class WebDriverOptions {

  private final Properties properties;
  @Getter private MutableCapabilities capabilities;
  @Getter @Setter private String pathString; // Path to store downloaded web driver executable
  @Getter private String version; // Desired WebDriver version
  @Getter private long implicitWaitSec;
  @Getter private long pageWaitSec;

  /**
   * Constructor class.
   *
   * @param propertiesFileName full file name of the driver properties file
   */
  WebDriverOptions(String propertiesFileName) {
    properties = WebDriverHelper.getProperties(propertiesFileName);
    createBrowserOptions();
    readDriverProperties();
  }

  public String getProperty(String propertyName) {
    return properties.getProperty(propertyName);
  }

  /** Creates browser options from an external properties file. */
  private void createBrowserOptions() {
    capabilities = new MutableCapabilities();
    capabilities.setCapability(
        CapabilityType.ACCEPT_SSL_CERTS,
        Boolean.parseBoolean(properties.getProperty("accept.ssl.certs")));
    capabilities.setCapability(
        CapabilityType.ACCEPT_INSECURE_CERTS,
        Boolean.parseBoolean(properties.getProperty("accept.insecure.certs")));
  }

  /** Reads other web driver properties. */
  private void readDriverProperties() {
    version = properties.getProperty("webdriver.version");
    implicitWaitSec = Long.parseLong(properties.getProperty("implicit.wait.sec"));
    pageWaitSec = Long.parseLong(properties.getProperty("page.wait.sec"));
  }
}
