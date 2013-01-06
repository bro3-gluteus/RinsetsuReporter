package m17.poo.daitoku;

import org.openqa.selenium.WebDriver;

public class CommonFlow {
  /**
   * mixiログインからブラ三サーバ選択後まで共通するフローを行い、
   * その時点のウェブドライバーを返す。
   * 
   * @return web driver
   */
  public static WebDriver getBro3WebDriver(boolean useFF) {
    MixiAccount mixiAccount = new MixiAccount();
    return getBro3WebDriver( useFF, mixiAccount.getMixiEmail(), mixiAccount.getMixiPassword() );
  }
  
  /**
   * mixiログインからブラ三サーバ選択後まで共通するフローを行い、
   * その時点のウェブドライバーを返す。
   * 
   * @return web driver
   */
  public static WebDriver getBro3WebDriver(boolean useFF, String mixiEmail, String mixiPassword) {
    MixiAccount mixiAccount = new MixiAccount(mixiEmail, mixiPassword);
    return getBro3WebDriver( useFF, mixiAccount.getMixiEmail(), mixiAccount.getMixiPassword(), 
            CommonSettings.SERVER );
  }
  
  public static WebDriver getBro3WebDriver(boolean useFF, 
          String mixiEmail, String mixiPassword, int server) {
    MixiAccount mixiAccount = new MixiAccount(mixiEmail, mixiPassword);
    WebDriver d = WebDriverFactory.createDriver(useFF);
    LogInAgent.logInMixi(d, mixiAccount);
    LogInAgent.selectBro3Server(d, server);
    d.switchTo().frame("mainframe"); // さらに中のiframe内にフォーカスを移す
    return d;
  }
}
