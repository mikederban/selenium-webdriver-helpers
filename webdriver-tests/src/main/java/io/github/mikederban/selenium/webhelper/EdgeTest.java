package io.github.mikederban.selenium.webhelper;

import org.assertj.core.api.AssertionsForClassTypes;
import org.openqa.selenium.WebDriver;

/**
 * Edge WebDriver test class.
 *
 * @author Mike Derban
 * @since 2021-11-06
 */
public final class EdgeTest {

  private EdgeTest() {}

  public static void main(String... args) {
    // Using default constructor.
    // You can specify a custom properties file which contains your desired WebDriver properties.
    // You can specify a custom download directory instead od system temp location.
    EdgeDriverHelper edgeDriver = new EdgeDriverHelper();
    edgeDriver.initialize();
    WebDriver driver = edgeDriver.getDriver();

    driver.navigate().to("https://google.ca/");
    AssertionsForClassTypes.assertThat(driver.getTitle()).isEqualTo("Google");

    driver.quit();
  }
}
