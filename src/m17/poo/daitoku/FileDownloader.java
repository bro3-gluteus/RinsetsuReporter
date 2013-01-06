package m17.poo.daitoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;


//Incomplete
public class FileDownloader {

  private static boolean followRedirects = true;
  private static boolean mimicWebDriverCookieState = true;

  /**
   * Load in all the cookies WebDriver currently knows about so that we can mimic the browser cookie
   * state
   * 
   * @param seleniumCookieSet
   * @return
   */
  private static BasicCookieStore mimicCookieState(Set<Cookie> seleniumCookieSet) {
    BasicCookieStore mimicWebDriverCookieStore = new BasicCookieStore();
    for (Cookie seleniumCookie : seleniumCookieSet) {
      BasicClientCookie duplicateCookie = new BasicClientCookie(seleniumCookie.getName(),
              seleniumCookie.getValue());
      duplicateCookie.setDomain(seleniumCookie.getDomain());
      duplicateCookie.setSecure(seleniumCookie.isSecure());
      duplicateCookie.setExpiryDate(seleniumCookie.getExpiry());
      duplicateCookie.setPath(seleniumCookie.getPath());
      mimicWebDriverCookieStore.addCookie(duplicateCookie);
    }

    return mimicWebDriverCookieStore;
  }

  @Deprecated
  /**
   * バグ：セッションタイムアウトになってしまう・・・。
   * 
   * 
   * Perform the file/image download.
   * 
   * @param element
   * @param attribute
   * @return
   * @throws IOException
   * @throws NullPointerException
   */
  public static String getContentFromUrlFast(WebDriver d, String fileToDownloadLocation) throws IOException,
          NullPointerException, URISyntaxException {
    URL fileToDownload = new URL(fileToDownloadLocation);

    HttpClient client = new DefaultHttpClient();
    BasicHttpContext localContext = new BasicHttpContext();

    // System.out.println("Mimic WebDriver cookie state: " + this.mimicWebDriverCookieState);
    if (mimicWebDriverCookieState) {
      localContext.setAttribute(ClientContext.COOKIE_STORE, mimicCookieState(d.manage()
              .getCookies()));
    }

    HttpGet httpget = new HttpGet(fileToDownload.toURI());
    httpget.setHeader("User-Agent", "5.0 (Macintosh; Intel Mac OS X 10_7_4) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.56 Safari/536.5");
    HttpParams httpRequestParameters = httpget.getParams();
    httpRequestParameters.setParameter(ClientPNames.HANDLE_REDIRECTS, followRedirects);
    httpget.setParams(httpRequestParameters);

    // System.out.println("Sending GET request for: " + httpget.getURI());
    HttpResponse response = client.execute(httpget, localContext);
    // this.httpStatusOfLastDownloadAttempt = response.getStatusLine().getStatusCode();
    // System.out.println("HTTP GET request status: " + this.httpStatusOfLastDownloadAttempt);
    // System.out.println("Downloading file: " + downloadedFile.getName());
    StringBuilder content = new StringBuilder();
    InputStream in = response.getEntity().getContent();
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    String line = null;
    while ((line = br.readLine()) != null) {
      content.append(line + "\n");
    }
    br.close();
    in.close();
    return content.toString();
  }
  
  public static String getContentFromUrlSlow(WebDriver d, String url) {
    d.navigate().to(url);
    String content = d.getPageSource();
    return content;
  }

}
