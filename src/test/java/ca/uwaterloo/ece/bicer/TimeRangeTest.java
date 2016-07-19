package ca.uwaterloo.ece.bicer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.jgit.api.Git;
import org.junit.Test;

import ca.uwaterloo.ece.bicer.utils.Utils;

public class TimeRangeTest {

	@Test
	public void test() {

		long milDay = 86400000;

		try {

			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // local timezone
			DateFormat formatterGMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // GMT 
			formatterGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
			
			//TimeZone here = TimeZone.getTimeZone("America/Toronto");
			//formatter.setTimeZone(here);
			Date tStartDate = formatterGMT.parse("2010-09-17 00:00:00 GMT");
			long trainDuration = (long)Math.round(365 * 0.1); // year
			long gap = (long)Math.round(365 * 0.1); // year
			long gapBetweenTrainingStartNTestStart = (long)Math.round(365 * 0.2);
			int updateTime = 30; // days

			
			

			for(int i=0; i<8; i++){
				Date trainStartDate = new Date(tStartDate.getTime() + (milDay * (i*updateTime)));
				System.out.println("\ntrainStartDate: " + formatter.format(trainStartDate) + " " + formatterGMT.format(trainStartDate));
				
				Date trainEndDate = new Date(trainStartDate.getTime() + (milDay * trainDuration) - 1 );
				System.out.println("trainEndDate: " + formatter.format(trainEndDate) + " " + formatterGMT.format(trainEndDate));
				
				Date testStartDate = new Date(trainStartDate.getTime() + (milDay * gapBetweenTrainingStartNTestStart));
				System.out.println("testStartDate: " + formatter.format(testStartDate) + " " + formatterGMT.format(testStartDate));
				
				Date testEndDate = new Date(testStartDate.getTime() + (milDay * updateTime) - 1);
				System.out.println("testEndDate: " + formatter.format(testEndDate) + " " + formatterGMT.format(testEndDate));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

}
