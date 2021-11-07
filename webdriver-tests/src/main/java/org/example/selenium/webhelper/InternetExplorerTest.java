package org.example.selenium.webhelper;

import org.assertj.core.api.AssertionsForClassTypes;
import org.openqa.selenium.WebDriver;

/**
 * Internet Explorer WebDriver test class.
 *
 * @author Mike Derban
 * @since 2021-11-06
 */
final class InternetExplorerTest {

  private InternetExplorerTest() {}

  public static void main(String... args) {
    // Using default constructor.
    // You can specify a custom properties file which contains your desired WebDriver properties.
    // You can specify a custom download directory instead od system temp location.
    IEDriverHelper ieDriver = new IEDriverHelper();
    ieDriver.initialize();
    WebDriver driver = ieDriver.getDriver();

    driver.navigate().to("https://google.ca/");
    AssertionsForClassTypes.assertThat(driver.getTitle()).isEqualTo("Google");

    driver.quit();
  }
}
