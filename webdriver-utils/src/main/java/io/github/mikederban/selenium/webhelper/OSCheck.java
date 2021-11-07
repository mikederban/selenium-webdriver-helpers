package io.github.mikederban.selenium.webhelper;

import java.util.Locale;

/** Helper class to check the operating system this Java VM runs in. */
public final class OSCheck {

  private OSCheck() {}

  /**
   * Detect the operating system from the os.name System property and cache the result.
   *
   * @return the operating system detected
   */
  public static OSType getOperatingSystemType() {
    OSType osType;
    String osString = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    if (osString.contains("mac") || osString.contains("darwin")) {
      osType = OSType.MAC_OS;
    } else if (osString.contains("win")) {
      osType = OSType.WINDOWS;
    } else if (osString.contains("nux")) {
      osType = OSType.LINUX;
    } else {
      osType = OSType.OTHER;
    }
    return osType;
  }

  /** Types of Operating Systems. */
  public enum OSType {
    WINDOWS,
    MAC_OS,
    LINUX,
    OTHER
  }
}
