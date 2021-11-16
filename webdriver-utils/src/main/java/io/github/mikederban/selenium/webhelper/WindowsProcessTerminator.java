package io.github.mikederban.selenium.webhelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This helper class contains method to terminate a running Windows process.
 *
 * @author Mike Derban
 * @since 2021-11-06
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WindowsProcessTerminator {

  private static final Logger LOGGER = LoggerFactory.getLogger(WindowsProcessTerminator.class);

  /**
   * Checks if the process is running.
   *
   * @param name process name
   * @return true/false
   */
  private static boolean isProcessRunning(String name) {
    AtomicBoolean flag = new AtomicBoolean(false);
    Process process = null;

    try {
      process = Runtime.getRuntime().exec("tasklist");
    } catch (IOException e) {
      LOGGER.error("Error running tasklist command!", e);
    }

    if (null != process) {
      try (BufferedReader reader =
          new BufferedReader(
              new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
        reader
            .lines()
            .forEach(
                line -> {
                  if (line.startsWith(name)) {
                    flag.set(true);
                  }
                });
      } catch (IOException e) {
        LOGGER.error(String.format("Error reading output from process %s!", name), e);
      } finally {
        process.destroy();
      }
    }

    return flag.get();
  }

  /**
   * Kills a Windows process by name.
   *
   * @param processName process name
   */
  public static void kill(String processName) {
    if (isProcessRunning(processName)) {
      Runtime rt = Runtime.getRuntime();

      Process process = null;
      try {
        final String[] command = {"cmd", "/c", "taskkill", "/f", "/im", processName};
        process = rt.exec(command);
      } catch (IOException e) {
        LOGGER.error(String.format("Error running process %s!", processName), e);
      }

      if (null != process) {
        try (BufferedReader br =
            new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
          br.lines().forEach(LOGGER::debug);
        } catch (IOException e) {
          LOGGER.error(String.format(" Error reading %s's output!", processName), e);
        }
      }

      if (null != process) {
        try {
          process.waitFor();
        } catch (InterruptedException e) {
          LOGGER.error(String.format("Failed to terminate process %s!", processName), e);
          Thread.currentThread().interrupt();
        } finally {
          process.destroy();
        }
      }
    }
  }
}
