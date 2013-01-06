package m17.poo.daitoku;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.openqa.selenium.WebDriver;

import com.google.gdata.util.ServiceException;

/**
 * NPC隣接状況を取得して隣接シートを更新
 */
public class RinsetsuReporter {
	
  private static int numThread = 8;
  private static int threadLife = 2*60*60;//スレッドの寿命(秒)

  private static Map<String,String[]> mapData= new ConcurrentHashMap<String,String[]>(258134);//11*11*1600*4/3
  
  public static void main(String[] args){
  	new Console();
  	System.out.println("[処理開始] "+numThread+"スレッドで同時にマップ情報の取得を開始します");
  	StopWatch sw = new StopWatch();
  	StopWatch swMap = new StopWatch();
    
    //設定ファイル
    ReadConfig cfg = new ReadConfig();
 
    //砦リストをスレッド数に分割
    TorideList tl = new TorideList();
    tl.setNumThread(numThread);
    List<List<String>> xyPairsPerThread = tl.getList();
    
    ExecutorService exec = Executors.newFixedThreadPool(numThread);
    for (int i=0; i<numThread; i++) {
      List<String> xyPairs = xyPairsPerThread.get(i);

      exec.execute(new RinsetsuReporterThread(xyPairs, mapData, i+1));
    }
    try {
      exec.shutdown();
      // (全てのタスクが終了した場合、trueを返してくれる)
      if (!exec.awaitTermination(threadLife, TimeUnit.SECONDS)) {
        // タイムアウトした場合、全てのスレッドを中断(interrupted)してスレッドプールを破棄する。
        exec.shutdownNow();
      }
    } catch (InterruptedException e) {
      // awaitTerminationスレッドがinterruptedした場合も、全てのスレッドを中断する
      System.out.println("awaitTermination interrupted: " + e);
      exec.shutdownNow();
    }
    
    System.out.println("---------マップ情報取得完了---------");
    swMap.stop("マップ情報取得時間");
    
    //出来上がったmapDataから隣接情報を分析
    StopWatch swAna = new StopWatch();
    RinsetsuAnalyzer rinsetsu = new RinsetsuAnalyzer(mapData);
    Map<String,String[]> output = rinsetsu.getOutput(cfg);
    swAna.stop("隣接情報分析完了");
    
    System.out.println();
    System.out.println("Googleスプレッドシート更新を開始します。");
   
    //ワークシート名を指定してデータの更新
    String gmail = cfg.getConfigList("email").get(0).trim();
    String password = cfg.getConfigList("password").get(0).trim();
    String sheetKey = cfg.getConfigList("sheetKey").get(0).trim();
    DataUploaderMulti uploda;
		try {
			uploda = new DataUploaderMulti(gmail,password,sheetKey);
			uploda.upload(output);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, 
					"想定外のエラーが発生した模様です",
					"IOException",
					JOptionPane.ERROR_MESSAGE
					);
			System.exit(-1);
		} catch (ServiceException e) {
			JOptionPane.showMessageDialog(null, 
					"想定外のエラーが発生した模様です",
					"ServiceException",
					JOptionPane.ERROR_MESSAGE
					);
			System.exit(-1);
		}
    

    System.out.println("----------- 全処理終了 -----------");
    sw.stop("合計処理時間");
		JOptionPane.showMessageDialog(null, 
				"全処理が終了しました",
				"Finish",
				JOptionPane.INFORMATION_MESSAGE	
				);
		System.exit(0);
  }
}

class RinsetsuReporterThread implements Runnable {
  private List<String> xyPairs;
  private int threadNumber;
  
  private Map<String, String[]> mapdata;
  
  public RinsetsuReporterThread(List<String> xyPairs, Map<String, String[]> mapdata, int threadNumber) {
    this.xyPairs = xyPairs;
    this.mapdata = mapdata;
    this.threadNumber = threadNumber;
  }

  public void run() {
    WebDriver d = CommonFlow.getBro3WebDriver(CommonSettings.USE_FIREFOX);
    int counter = 0;
    for (String xy : xyPairs) {
      try {
        StopWatch sw = new StopWatch();
        String[] items = xy.split(",");
        String x = items[0];
        String y = items[1];
        String mapURL = "http://m" + CommonSettings.SERVER + ".3gokushi.jp/map.php?x=" + x + "&y="
                + y + "&type=1"; //今回は11✕11マップで十分なので。
        String srcOf11x11Map = FileDownloader.getContentFromUrlSlow(d, mapURL);

        ParserFor11x11Map.loadRawHtml(mapdata, srcOf11x11Map);
        sw.stop("スレッドID="+threadNumber+"が ("+x+","+y+") 中心の11×11マップから領地情報を取得しました。　("+(++counter)+"/"+xyPairs.size()+")");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}