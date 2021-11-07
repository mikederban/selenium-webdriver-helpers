package io.github.mikederban.selenium.webhelper;

import io.github.mikederban.selenium.webhelper.OSCheck.OSType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.time.Duration;
import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Web driver helper class.
 *
 * @author Mike Derban
 * @since 2021-11-06
 */
public abstract class WebDriverHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverHelper.class);
  private static final String WEBDRIVER_FILENAME_SUFFIX_WIN = ".exe";

  @Getter @Setter private WebDriver driver;
  @Getter private WebDriverOptions webDriverOptions;
  @Getter private String pathString; // Location of the downloaded web driver
  private String proxyServer;
  @Getter private String version;
  private String webdriverDownloadUrl;
  private String webdriverFilename;
  private String webdriverArchiveFilename;
  private String webdriverSystemPropertyName;

  /**
   * Initializes an instance of Selenium web driver using an external property file.
   *
   * @param propsFileName an external property file name
   * @param downloadDir destination download directory for webdriver executable
   */
  protected WebDriverHelper(String propsFileName, File downloadDir) {
    setVariables(propsFileName);
    prepare(propsFileName, downloadDir);
  }

  /**
   * Loads properties from properties file.
   *
   * @param propsFileName properties file name
   * @return properties object
   */
  @SneakyThrows(IOException.class)
  public static Properties getProperties(String propsFileName) {
    InputStream fs = new ResourceLoader().getResourceAsStream(propsFileName);
    Properties properties = new Properties();
    properties.load(fs);
    return properties;
  }

  /**
   * Reads web driver download and file related attributes from properties file.
   *
   * @param propsFileName properties filename
   */
  private void setVariables(String propsFileName) {
    Properties properties = getProperties(propsFileName);
    webdriverDownloadUrl = properties.getProperty("webdriver.download.url");

    String wdFilenameSuffix = "";
    OSType osType = OSCheck.getOperatingSystemType();
    if (OSType.LINUX == osType) {
      webdriverArchiveFilename = properties.getProperty("webdriver.zip.filename.linux");
    } else if (OSType.MAC_OS == osType) {
      webdriverArchiveFilename = properties.getProperty("webdriver.zip.filename.mac");
    } else { // OSType.WINDOWS
      webdriverArchiveFilename = properties.getProperty("webdriver.zip.filename.win");
      wdFilenameSuffix = String.format("%s%s", wdFilenameSuffix, WEBDRIVER_FILENAME_SUFFIX_WIN);
    }

    webdriverFilename =
        String.format("%s%s", properties.getProperty("webdriver.filename"), wdFilenameSuffix);

    webdriverSystemPropertyName = properties.getProperty("webdriver.system.property.name");

    proxyServer =
        null == properties.getProperty("proxy.server")
            ? ""
            : properties.getProperty("proxy.server");
  }

  /**
   * Downloads WebDriver and sets system variable.
   *
   * @param filename properties file name.
   * @param downloadFolder destination folder to download web driver
   */
  private void prepare(String filename, File downloadFolder) {
    if (OSType.WINDOWS == OSCheck.getOperatingSystemType()) {
      WindowsProcessTerminator.kill(webdriverFilename);
    }
    String dlFolder = null == downloadFolder ? null : downloadFolder.getAbsolutePath();
    webDriverOptions = new WebDriverOptions(filename);
    if (proxyServer.isEmpty()) {
      downloadWebDriver(webDriverOptions.getVersion(), dlFolder);
    } else {
      downloadWebDriver(webDriverOptions.getVersion(), dlFolder, proxyServer);
    }

    System.setProperty(webdriverSystemPropertyName, getWebdriverFullFileName());
  }

  /** Loads additional options specific to WebDriver implementation and initializes WebDriver. */
  public abstract void initialize();

  /**
   * Downloads web driver exe.
   *
   * @param ver desired WebDriver version
   * @param downloadDir destination directory to download web driver
   * @param proxyServerOptional proxy server. Usually CI build agents don't have direct access to
   *     Internet, and you need to use a proxy server
   */
  private void downloadWebDriver(String ver, String downloadDir, String... proxyServerOptional) {
    WebDriverDownload dl =
        new WebDriverDownload(
            ver,
            webdriverDownloadUrl,
            webdriverArchiveFilename,
            webdriverFilename,
            downloadDir,
            proxyServerOptional);
    version = dl.getVersion(); // set actual version of web driver for reference
    String message =
        String.format("Attempting to download web driver, target version is [%s]", version);
    LOGGER.info(message);
    dl.download(version);
    pathString = dl.getDestPathString();
    webDriverOptions.setPathString(pathString);
  }

  /**
   * Sets WebDriver properties.
   *
   * @param driverOptions properties filename
   */
  void initWebDriver(WebDriverOptions driverOptions) {
    driver
        .manage()
        .timeouts()
        .implicitlyWait(Duration.ofSeconds(driverOptions.getImplicitWaitSec()));
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(driverOptions.getPageWaitSec()));
    driver.manage().window().maximize();
  }

  /**
   * Returns fully qualified web driver file name.
   *
   * @return fully qualified web driver file name
   */
  public String getWebdriverFullFileName() {
    return String.format(
        "%s%s%s",
        webDriverOptions.getPathString(),
        FileSystems.getDefault().getSeparator(),
        webdriverFilename);
  }
}
