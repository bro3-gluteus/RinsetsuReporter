package m17.poo.daitoku;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Link;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

class DataUploaderThread implements Runnable{
	private SpreadsheetService service;
	private GoogleSheet googleSheet;
	private String wsName;
	private String range;
	private Map<String,String[]> output;
	
	public DataUploaderThread(GoogleSheet googleSheet,SpreadsheetService service, String wsName,String range,Map<String,String[]> output) throws IOException, ServiceException {
		this.service = service;
		this.googleSheet = googleSheet;
		this.wsName = wsName;
		this.range = range;
		this.output=output;
	}

	public void run() {
		StopWatch sw = new StopWatch();
		WorksheetEntry worksheet;
		try {
			worksheet = googleSheet.getWorksheet(wsName);
			CellQuery cellQuery = new CellQuery(worksheet.getCellFeedUrl());
			cellQuery.setRange(range);
			cellQuery.setReturnEmpty(true); // 空セルも返すようにする
			CellFeed cellFeed = service.query(cellQuery, CellFeed.class);
	    
			CellFeed batchFeed = new CellFeed();
			for (int i = 0; i < cellFeed.getEntries().size(); i=i+6) {
				
				String key = cellFeed.getEntries().get(i).getPlainTextContent();
				//System.out.println(key);
				for (int j=0;j<5;j++){
					CellEntry cellentry = cellFeed.getEntries().get(i+j+1);
					if (output.get(key)!=null) {
						//System.out.println(output.get(key)[j]);
						cellentry.changeInputValueLocal(output.get(key)[j]);
					}
					BatchUtils.setBatchId(cellentry, cellentry.getTitle().getPlainText());
					BatchUtils.setBatchOperationType(cellentry, BatchOperationType.UPDATE);
					batchFeed.getEntries().add(cellentry);
				}
				
			}
			Link batchLink = cellFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM);
			service.getRequestFactory().setHeader("If-Match", "*");
			service.batch(new URL(batchLink.getHref()), batchFeed);
			service.getRequestFactory().setHeader("If-Match", null);
			sw.stop(wsName+"シートを更新しました");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}	
	}
}
