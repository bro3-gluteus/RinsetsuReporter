package m17.poo.daitoku;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Util {
  
  public static String getTimestamp() {
    GregorianCalendar gcalendar = new GregorianCalendar();
    String yyyy = gcalendar.get(Calendar.YEAR)+"";
    String mm = (gcalendar.get(Calendar.MONTH)+1)+"";
    if (mm.length()==1) mm = "0"+mm;
    String dd = (gcalendar.get(Calendar.DATE))+"";
    if (dd.length()==1) dd = "0"+dd;
    String HH = (gcalendar.get(Calendar.HOUR_OF_DAY))+"";
    if (HH.length()==1) HH = "0"+HH;
    String MM = (gcalendar.get(Calendar.MINUTE))+"";
    if (MM.length()==1) MM = "0"+MM;
    return yyyy+mm+dd+"_"+HH+MM;
  }

}
