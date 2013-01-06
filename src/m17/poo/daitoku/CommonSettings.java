package m17.poo.daitoku;

//共通パラメータ
//もし変更が頻繁に起きるようならメイン関数の引数や
//外部ファイルで値を指定できるようにする。
public class CommonSettings {

  // m17鯖以外にログインする場合はここを編集。
  public static int SERVER = 17;

  // true:Firefoxで開く、false:何も開かないで処理
  // ぶらんくの環境ではなぜか前者30秒、後者60秒ぐらい。
  public static boolean USE_FIREFOX = false;

  static {
    StopWatch.PRINT_LOG = true;// ログが出るように設定。
  }

}
