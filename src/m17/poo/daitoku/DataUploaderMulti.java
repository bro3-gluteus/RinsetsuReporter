package m17.poo.daitoku;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.util.ServiceException;

public class DataUploaderMulti {

	private GoogleSheet googleSheet;
	private SpreadsheetService service;
	private String[][] wsList = {
																	{"全NPC隣接情報","D2:I1549"},
																	{"城・武将砦隣接情報","D2:I30"},
																	{"南東砦隣接情報","D2:I380"},
																	{"南西砦隣接情報","D2:I380"},
																	{"北東砦隣接情報","D2:I388"},
																	{"北西砦隣接情報","D2:I376"}
																};
	private Map<String,String[]> output;
	
	public DataUploaderMulti(String email,String password,String key) throws IOException, ServiceException{
		this.googleSheet = new GoogleSheet(email,password,key);
		this.service = googleSheet.getService();
	}
	
	public void upload(Map<String,String[]> output) throws IOException, ServiceException{
		this.output = output;
		ExecutorService exec = Executors.newFixedThreadPool(3); //今の所３スレッドが一番高速
		for (int i=0;i<6;i++)
		exec.execute(new DataUploaderThread(googleSheet, service, wsList[i][0], wsList[i][1],this.output));
    try {
      exec.shutdown();
      // (全てのタスクが終了した場合、trueを返してくれる)
      if (!exec.awaitTermination(400, TimeUnit.SECONDS)) {
        // タイムアウトした場合、全てのスレッドを中断(interrupted)してスレッドプールを破棄する。
        exec.shutdownNow();
      }
    } catch (InterruptedException e) {
      // awaitTerminationスレッドがinterruptedした場合も、全てのスレッドを中断する
      System.out.println("awaitTermination interrupted: " + e);
      exec.shutdownNow();
    }
	}
	
}

