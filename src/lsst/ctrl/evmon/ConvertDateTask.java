package lsst.ctrl.evmon;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import lsst.ctrl.evmon.engine.EventStore;
import lsst.ctrl.evmon.engine.MonitorMessage;

/*
 * val = 2009-04-21T13:09:18.8856217, eval = 1240346214217
 * val = 2009-04-21T13:09:47.7364924, eval = 1240344751924
 */
public class ConvertDateTask implements Task {
	String result;
	String dateString;
	SimpleDateFormat dateFormat = null;
	Calendar cal = new GregorianCalendar();
	BigDecimal milliMult = new BigDecimal(1000);
	
	public ConvertDateTask(String result, String dateString) {
		this.result = result;
		this.dateString = dateString;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
	}

	public void execute(EventStore es, MonitorMessage msg) {
			String val = es.lookup(dateString);
			Date date = null;
			try {
				date = dateFormat.parse(val);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cal.setTime(date);
			long eval = cal.getTimeInMillis();

			int lastColon = val.lastIndexOf(":");
			String seconds = val.substring(lastColon+1);
			BigDecimal bd = new BigDecimal(seconds);
			BigDecimal millis = bd.multiply(milliMult);
			BigDecimal initValue = new BigDecimal(eval);
			BigDecimal finalValue = initValue.add(millis);
			long fValue = finalValue.longValue();
/*			
			System.out.println("val = "+val+", eval = "+eval);
			System.out.println("seconds = "+seconds);
			System.out.println("millis ="+millis);
			System.out.println("setting eval = "+fValue);
*/
			es.put(result, ""+fValue);

	}
	
	public static void main(String[] args) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		Calendar cal = new GregorianCalendar();		
		Date date = null;
		try {
			date = dateFormat.parse("2009-04-21T15:18:02:22.2519227");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cal.setTime(date);
		long eval = cal.getTimeInMillis();
		System.out.println(eval);
		System.out.println(new Date(eval));
		
		eval += 22251.9927;
		System.out.println(eval);
		System.out.println(new Date(eval));
	}

}