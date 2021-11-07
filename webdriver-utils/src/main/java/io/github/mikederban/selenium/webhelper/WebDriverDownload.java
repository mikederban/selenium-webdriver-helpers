package io.github.mikederban.selenium.webhelper;

import io.github.mikederban.selenium.webhelper.OSCheck.OSType;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.Getter;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Downloader for web driver executables.
 *
 * @author Mike Derban
 * @since 2021-11-06
 */
public final class WebDriverDownload {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverDownload.class);
  private static final Pattern PATTERN_NON_ALPHA = Pattern.compile("[\\x00]|([\r\n])|[\\uFFFD]");
  private static final String STRING_FORMAT_PATH_FILENAME = "%s%s%s";
  private static final String STRING_FORMAT_DOWNLOAD_ERROR = "Error downloading [%s]";
  private static final int TIMEOUT_CONNECT_MS = 5 * 1000;
  private static final int TIMEOUT_READ_MS = 5 * 60 * 1000;

  @Getter private final String destPathString;
  private final String fileDownloadUrl;
  private final String proxyServer;
  @Getter private final String version;
  private final String webdriverFilename;
  private final String webdriverArchiveFilename;

  /**
   * Default constructor.
   *
   * @param versionString chromedriver.exe version to download, e.g. 86.0.4240.22, or
   *     LATEST_RELEASE_86, or LATEST_RELEASE
   * @param webdriverArchiveFilename web driver zip file name
   * @param webdriverFilename web driver exe file name
   * @param downloadDir destination directory for downloaded web driver executable
   * @param proxy an optional proxy server. Usually CI build agents don't have direct access to
   *     Internet, and you need to use a proxy server
   */
  WebDriverDownload(
      String versionString,
      String fileDownloadUrl,
      String webdriverArchiveFilename,
      String webdriverFilename,
      String downloadDir,
      String... proxy) {

    this.fileDownloadUrl = fileDownloadUrl;
    this.webdriverArchiveFilename = webdriverArchiveFilename;
    this.webdriverFilename = webdriverFilename;
    proxyServer = 0 == proxy.length ? "" : proxy[0];
    version = setVersion(versionString);
    String uuid = String.format("{%s}", UUID.randomUUID().toString().toUpperCase(Locale.ENGLISH));
    destPathString =
        null == downloadDir
            ? String.format(
                STRING_FORMAT_PATH_FILENAME,
                getTermFolderName(),
                FileSystems.getDefault().getSeparator(),
                uuid)
            : downloadDir;
  }

  /**
   * Returns proxy server URI.
   *
   * @param proxyStr proxy string, e.g. "http://proxy.server:8080/"
   * @return proxy server URI object
   */
  private static URI getProxyURI(String proxyStr) {
    URI uri;
    try {
      uri = new URI(proxyStr);
    } catch (URISyntaxException e) {
      LOGGER.error(
          String.format("Malformed proxy host URI [%s]. Proxy will not be set", proxyStr), e);
      uri = null;
    }
    return uri;
  }

  /**
   * Returns proxy server host name.
   *
   * @param proxyStr proxy string, e.g. "http://proxy.server:8080/"
   * @return host name
   */
  private static String getProxyHost(String proxyStr) {
    URI uri = getProxyURI(proxyStr);
    return null == uri ? "" : uri.getHost();
  }

  /**
   * Returns proxy server port number.
   *
   * @param proxyStr proxy string, e.g. "http://proxy.server:8080/"
   * @return host name
   */
  private static int getProxyPort(String proxyStr) {
    URI uri = getProxyURI(proxyStr);
    return null == uri ? -1 : uri.getPort();
  }

  /**
   * Determines temp folder name depending on OS type.
   *
   * @return temp folder name
   */
  private static String getTermFolderName() {
    String tempFolder;
    OSType osType = OSCheck.getOperatingSystemType();
    if (OSType.LINUX == osType || OSType.MAC_OS == osType) {
      tempFolder = System.getenv("TMPDIR");
    } else { // OSType.WINDOWS
      tempFolder = System.getenv("TEMP");
    }
    return tempFolder;
  }

  /**
   * Retrieves the actual version of ChromeDriver to download.
   *
   * @param versionString version provided in the properties file
   * @return actual version of ChromeDriver
   */
  private String setVersion(String versionString) {
    String fullVersion = null;
    if (versionString.startsWith("LATEST")) {
      try {
        URLConnection httpConn = getURLConnection(getVersionFileUrl(versionString));
        if (null == httpConn) {
          LOGGER.error("Connection was not established. WebDriver version was not retrieved");
        } else {
          fullVersion = IOUtils.toString(httpConn.getInputStream(), StandardCharsets.UTF_8);
          // Cleaning up version string from edge download site
          fullVersion = PATTERN_NON_ALPHA.matcher(fullVersion).replaceAll("");
        }
      } catch (MalformedURLException e) {
        LOGGER.error(String.format("Malformed URL [%s]", getVersionFileUrl(versionString)), e);
      } catch (IOException e) {
        LOGGER.error(
            String.format(STRING_FORMAT_DOWNLOAD_ERROR, getVersionFileUrl(versionString)), e);
      }
    } else {
      fullVersion = versionString;
    }
    return fullVersion;
  }

  /**
   * Returns URL string of driver version info file.
   *
   * @param verStr target version
   * @return URL string
   */
  private String getVersionFileUrl(String verStr) {
    return String.format("%s/%s", fileDownloadUrl, verStr);
  }

  /**
   * Returns connection.
   *
   * @param urlString download URL
   * @return connection
   */
  private URLConnection getURLConnection(String urlString) {
    Proxy proxy;
    if (proxyServer.isEmpty()) {
      proxy = null;
    } else {
      String host = getProxyHost(proxyServer);
      int port = getProxyPort(proxyServer);
      proxy = host.isEmpty() ? null : new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }
    URLConnection httpConn;
    try {
      if (null == proxy) {
        httpConn = new URL(urlString).openConnection();
      } else {
        httpConn = new URL(urlString).openConnection(proxy);
      }
      httpConn.setConnectTimeout(TIMEOUT_CONNECT_MS);
      httpConn.setReadTimeout(TIMEOUT_READ_MS);
    } catch (MalformedURLException e) {
      httpConn = null;
      LOGGER.error(String.format("Malformed URL [%s]", urlString), e);
    } catch (IOException e) {
      httpConn = null;
      LOGGER.error(String.format(STRING_FORMAT_DOWNLOAD_ERROR, urlString), e);
    }

    return httpConn;
  }

  /**
   * Downloads ChromeDriver zip archive, unzips it and copies to the place.
   *
   * @param ver target WebDriver version
   */
  public void download(String ver) {
    String tempArchiveFileName = getTempArchiveFileName();

    // Download to the temp location
    String msg1 = String.format("Retrieving data from [%s]...", getDriverDownloadUrl(ver));
    LOGGER.info(msg1);

    URLConnection httpConn = getURLConnection(getDriverDownloadUrl(ver));

    if (null == httpConn) {
      LOGGER.error("Connection was not established");
    } else {
      try {
        File tempArchiveFile = new File(tempArchiveFileName);
        FileUtils.copyInputStreamToFile(httpConn.getInputStream(), tempArchiveFile);
        String message2 =
            String.format("Downloaded [%s] version [%s]", tempArchiveFile.getName(), ver);
        LOGGER.info(message2);
      } catch (IOException e) {
        LOGGER.error(String.format(STRING_FORMAT_DOWNLOAD_ERROR, httpConn.getURL().getPath()), e);
      }

      if (tempArchiveFileName.endsWith(".zip")) {
        try (ZipFile zipFile = new ZipFile(tempArchiveFileName)) {
          // Unzip webdriver
          zipFile.extractAll(destPathString);
          String message =
              String.format(
                  "Webdriver [%s] is unpacked and ready to use in [%s]",
                  webdriverFilename, destPathString);
          LOGGER.info(message);
        } catch (IOException e) {
          LOGGER.error("Error un-zipping Webdriver archive!", e);
        }
      } else {
        throw new IllegalArgumentException(
            String.format("Archive [%s] format is unknown to the helper!", tempArchiveFileName));
      }
    }
  }

  /**
   * Returns fully qualified file name of local driver archive in the temp folder.
   *
   * @return zip file name
   */
  private String getDriverDownloadUrl(String ver) {
    return String.format("%s/%s/%s", fileDownloadUrl, ver, webdriverArchiveFilename);
  }

  /**
   * Returns fully qualified file name of local driver archive in the temp folder.
   *
   * @return zip file name
   */
  private String getTempArchiveFileName() {
    return String.format(
        STRING_FORMAT_PATH_FILENAME,
        destPathString,
        FileSystems.getDefault().getSeparator(),
        webdriverArchiveFilename);
  }
}
