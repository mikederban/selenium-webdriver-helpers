package org.example.selenium.webhelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.SneakyThrows;

/**
 * Searches all possible locations for a resource file.
 *
 * @author Mike Derban
 * @since 2021-11-06
 */
public final class ResourceLoader {

  /**
   * Loads resource.
   *
   * @param name resource file name
   * @return resource as an input stream
   */
  @SneakyThrows(IOException.class)
  public InputStream getResourceAsStream(String name) {
    InputStream is = null;
    URL url = getResourceUrl(name);
    if (null != url) {
      is = url.openStream();
    }
    return is;
  }

  /**
   * Searches for a resource file.
   *
   * @param name resource file name
   * @return resource URL
   */
  @SneakyThrows(MalformedURLException.class)
  private URL getResourceUrl(String name) {
    URL resource;
    resource = getClass().getResource(name);
    if (null == resource) {
      resource = Thread.currentThread().getContextClassLoader().getResource(name);
      if (null == resource) {
        resource = ClassLoader.getSystemResource(name);
        if (null == resource) {
          File file = new File(name);
          if (file.exists()) {
            resource = file.toURI().toURL();
          }
        }
      }
    }
    return resource;
  }
}
