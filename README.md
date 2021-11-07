# Selenium WebDriver helpers project #

The purpose of this project is to streamline the automated testing for desktop browsers and make it
easier. The proposed WebDriver helpers provide proper out-of-the-box configuration for automated
testing, and can easily be customized and tuned for specific project needs.

Every time a driver helper is instantiated, it will download a copy of Selenium WedDriver. You can
specify which version to use, or go with the default version, which is the latest build.

## Supported WebDrivers ##

1. webdriver-chrome
1. webdriver-edge
1. webdriver-ie

## Quick Start ##

### Chrome WebDriver ###

```xml
<dependency>
  <groupId>io.github.mikederban.selenium.webhelper</groupId>
  <artifactId>webdriver-chrome</artifactId>
  <version>1.0.0</version>
</dependency>
```

```java
ChromeDriverHelper chromeDriver = new ChromeDriverHelper();
chromeDriver.initialize();
WebDriver driver = chromeDriver.getDriver();
```

### Edge WebDriver ###

```xml
<dependency>
  <groupId>io.github.mikederban.selenium.webhelper</groupId>
  <artifactId>webdriver-edge</artifactId>
  <version>1.0.0</version>
</dependency>
```

```java
EdgeDriverHelper edgeDriver = new EdgeDriverHelper();
edgeDriver.initialize();
WebDriver driver = edgeDriver.getDriver();
```

### Internet Explorer WebDriver ###

```xml
<dependency>
  <groupId>io.github.mikederban.selenium.webhelper</groupId>
  <artifactId>webdriver-ie</artifactId>
  <version>1.0.0</version>
</dependency>
```

```java
IEDriverHelper ieDriver = new IEDriverHelper();
ieDriver.initialize();
WebDriver driver = ieDriver.getDriver();
```

## Default WebDriver configuration ##

Every helper is supplied with a pre-packaged properties file.

### Chrome WebDriver properties ###

```properties
# Web driver version. You can use the following:
# - an explicit version, e.g. 86.0.4240.22
# - the latest release of particular version, e.g. LATEST_RELEASE_86
# - the latest release: LATEST_RELEASE
webdriver.version=LATEST_RELEASE
# Proxy server to use when running on restricted boxes, i.e. build agents
# proxy.server=http://proxy.server:8080/
accept.insecure.certs=true
accept.ssl.certs=true
implicit.wait.sec=5
page.wait.sec=60
# Helpful command line switches
# http://peter.sh/experiments/chromium-command-line-switches/
browser.options=--disable-blink-features=BlockCredentialedSubresources,--disable-crash-reporter,\
  --disable-dev-shm-usage,--disable-extensions,--disable-gpu,--disable-in-process-stack-traces,\
  --disable-logging,--ignore-certificate-errors,--log-level=3,--no-sandbox,--output=/dev/null
# No changes required below this line
webdriver.download.url=https://chromedriver.storage.googleapis.com
webdriver.filename=chromedriver
webdriver.zip.filename.linux=chromedriver_linux64.zip
webdriver.zip.filename.mac=chromedriver_mac64.zip
webdriver.zip.filename.win=chromedriver_win32.zip
webdriver.system.property.name=webdriver.chrome.driver
```

### Edge WebDriver properties ###

```properties
# Web driver version. You can use the following:
# - an explicit version, e.g. 89.0.713.0
# - the latest release: LATEST_STABLE
webdriver.version=LATEST_STABLE
# Proxy server to use when running on restricted boxes, i.e. build agents
# proxy.server=http://proxy.server:8080/
accept.insecure.certs=true
accept.ssl.certs=true
implicit.wait.sec=5
page.wait.sec=60
# No changes required below this line
webdriver.download.url=https://msedgedriver.azureedge.net
webdriver.filename=msedgedriver
webdriver.zip.filename.win=edgedriver_win32.zip
webdriver.system.property.name=webdriver.edge.driver
```

### Internet Explorer WebDriver properties ###

```properties
# Web driver version. You can use the following:
# - an explicit version, e.g. 3.150
# Modify webdriver.zip.filename below when updating webdriver.version!
webdriver.version=3.150
# Proxy server to use when running on restricted boxes, i.e. build agents
# proxy.server=http://proxy.server:8080/
# The IE driver does not allow bypassing insecure (self-signed) SSL certificates
accept.insecure.certs=false
accept.ssl.certs=true
implicit.wait.sec=5
page.wait.sec=60
webdriver.download.url=https://selenium-release.storage.googleapis.com
webdriver.filename=IEDriverServer
webdriver.zip.filename.win=IEDriverServer_Win32_3.150.1.zip
webdriver.system.property.name=webdriver.ie.driver
```

## WebDriver custom properties file and download directory ##

By default, WebDriver executable is downloaded into a random UUID named directory located in the
system temporary directory to avoid resource lock during multi-threaded testing. You can specify a
custom target directory, if necessary:

```java
File downloadDir=new File("c:\\temp");
    ChromeDriverHelper chromeDriver=new ChromeDriverHelper(downloadDir);
```

You can use pre-packaged properties file, or specify a custom one.

```java
String propsName="C:\\projects\\driver-config\\chrome-driver.properties";
    ChromeDriverHelper chromeDriver=new ChromeDriverHelper(propsName);
```

A custom properties file and a custom download directory:

```java
String propsName="C:\\projects\\driver-config\\chrome-driver.properties";
    File downloadDir=new File("C:\\temp");
    ChromeDriverHelper chromeDriver=new ChromeDriverHelper(propsName,downloadDir);
```

Happy testing!
