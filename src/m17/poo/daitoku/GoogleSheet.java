package m17.poo.daitoku;

import java.io.IOException;
import java.net.URL;

import javax.swing.JOptionPane;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.client.spreadsheet.WorksheetQuery;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class GoogleSheet {

	private SpreadsheetEntry spreadsheet;
	private SpreadsheetService service;
	
	public GoogleSheet(String username, String password, String key) throws IOException{
		service = new SpreadsheetService("applicationName");
		try {
			service.setUserCredentials(username, password);
		} catch (AuthenticationException e) {
			JOptionPane.showMessageDialog(null, 
					"Googleサービスへのログインに失敗しました。\n設定ファイルを見なおしてください。",
					"AuthenticationException",
					JOptionPane.ERROR_MESSAGE
					);
			System.exit(-1);
		}
		URL entryUrl = new URL("http://spreadsheets.google.com/feeds/spreadsheets/" + key);
		try {
			spreadsheet = service.getEntry(entryUrl, SpreadsheetEntry.class);
		} catch (ServiceException e) {
			JOptionPane.showMessageDialog(null, 
					"Spreadsheetが見つかりませんでした。\nGoogleドライブのマイドライブに目的のSpreadsheetが入っていますか？",
					"ServiceException",
					JOptionPane.ERROR_MESSAGE
					);
			System.exit(-1);
		} 
	}
	
	public GoogleSheet(String username, String password){
		service = new SpreadsheetService("applicationName");
		try {
			service.setUserCredentials(username, password);
		} catch (AuthenticationException e) {
			JOptionPane.showMessageDialog(null, 
					"Googleサービスへのログインに失敗しました。\n設定ファイルを見なおしてください。",
					"AuthenticationException",
					JOptionPane.ERROR_MESSAGE
					);
			System.exit(-1);
		} 
	}
	
	public GoogleSheet(){
		service = new SpreadsheetService("applicationName");
	}
	
	public SpreadsheetService getService(){
		return service;
	}
	
	public SpreadsheetEntry getSpreadsheet(String key) throws IOException{
		URL entryUrl = new URL("http://spreadsheets.google.com/feeds/spreadsheets/" + key);
		SpreadsheetEntry spreadsheet = null;
		try {
			spreadsheet = service.getEntry(entryUrl, SpreadsheetEntry.class);
		} catch (ServiceException e) {
			JOptionPane.showMessageDialog(null, 
					"Spreadsheetが見つかりませんでした。\nGoogleドライブのマイドライブに目的のSpreadsheetが入っていますか？",
					"ServiceException",
					JOptionPane.ERROR_MESSAGE
					);
			System.exit(-1);
		}

		return spreadsheet;
	}
	
	public WorksheetEntry getWorksheet(String title) throws IOException{
		WorksheetQuery worksheetQuery = new WorksheetQuery(spreadsheet.getWorksheetFeedUrl());
		worksheetQuery.setTitleQuery(title); 
		WorksheetFeed worksheetFeed = null;
		try {
			worksheetFeed = spreadsheet.getService().query(worksheetQuery, WorksheetFeed.class);
		} catch (ServiceException e) {
			JOptionPane.showMessageDialog(null, 
					"Worksheetの名前が変更されてる可能性があります。確認してください。",
					"ServiceException",
					JOptionPane.ERROR_MESSAGE
					);
			System.exit(-1);
		}
		WorksheetEntry worksheetEntry = worksheetFeed.getEntries().get(0);
		return worksheetEntry;
	}
}
