package com.drew.github;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class WriteCSV {

	/**
	 * @param args
	 */
	
	static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	public static void main(ArrayList<TripBean> tripBeanList, String sFileName, Boolean lite) {
		
		try
		{
			FileWriter writer = new FileWriter(sFileName);
	 
			// headers
		    writer.append("start_time");
		    writer.append(',');
		    
		    // if this is a 'lite' table, don't print these
		    if(!lite){
		    	
		    	writer.append("stationFromId");
			    writer.append(',');
			    writer.append("stationFromLat");
			    writer.append(',');
			    writer.append("stationFromLong");
			    writer.append(',');
			    writer.append("stationToId");
			    writer.append(',');
			    writer.append("stationToLat");
			    writer.append(',');
			    writer.append("stationToLong");
			    writer.append(',');
		    }
		    
		    writer.append("distance");
		    writer.append(',');
		    writer.append('\n');
	 
		    //for(int k=0;k<tripBeanList.size();k++){
		    for(int k=0;k<tripBeanList.size();k++){

		    	// if this is a 'lite' table, don't print these
		    	if(!lite){
		    		
			    	// start time
			    	String dateStr = df.format(tripBeanList.get(k).getStartTime());
			    	writer.append(dateStr);
			    	writer.append(',');
		    		
		    		// station FROM Id
			    	String fromIdStr = String.valueOf(tripBeanList.get(k).getStationIdFrom());
			    	writer.append(fromIdStr);
			    	writer.append(',');
			    	
			    	// station FROM LAT
			    	String fromLatStr = String.valueOf(tripBeanList.get(k).getLatFrom());
			    	writer.append(fromLatStr);
			    	writer.append(',');
			    	
			    	// station FROM LONG
			    	String fromLongStr = String.valueOf(tripBeanList.get(k).getLongFrom());
			    	writer.append(fromLongStr);
			    	writer.append(',');
			    	
			    	// station TO Id
			    	String toIdStr = String.valueOf(tripBeanList.get(k).getStationIdTo());
			    	writer.append(toIdStr);
			    	writer.append(',');
			    	
			    	// station TO LAT
			    	String toLatStr = String.valueOf(tripBeanList.get(k).getLatTo());
			    	writer.append(toLatStr);
			    	writer.append(',');
			    	
			    	// station TO LONG
			    	String toLongStr = String.valueOf(tripBeanList.get(k).getLongTo());
			    	writer.append(toLongStr);
			    	writer.append(',');
			    	
			    	// distance (miles)
			    	String distStr = String.valueOf(tripBeanList.get(k).getDistance());
			    	writer.append(distStr);
		    	}else{
		    		
			    	// start time
			    	String dateStr = df.format(tripBeanList.get(k).getStartTime());
			    	writer.append(dateStr);
			    	writer.append(',');
			    	
			    	// distance (miles)
			    	String distStr = String.valueOf(tripBeanList.get(k).getDistance());
			    	writer.append(distStr);
		    	}

		    	// next line
		        writer.append('\n');
		    }
	 
		    writer.flush();
		    writer.close();
		    System.out.println("Wrote CSV file to " + sFileName);
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	}
	
	public static void makeTempo(ArrayList<TripBean> tripBeanList, String sFileName, Boolean bearing){
		
		try
		{
			System.out.println("tblist size: " + tripBeanList.size());
			
			// DaaS requires timestamps in ISO8601 format
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			FileWriter writer = new FileWriter(sFileName);
	 
		    for(int k=0;k<tripBeanList.size();k++){
		    	
		    	// start time
		    	String dateStr = df.format(tripBeanList.get(k).getStartTime());
		        
		        // to avoid collisions with DaaS, randomize the seconds & milliseconds
		        Random rs = new Random();
		        int sec = Math.abs(rs.nextInt()%59);
		        String secStr = String.valueOf(sec);
		        if(sec<10){
		        	secStr = "0" + secStr;
		        }
		        
		        Random rms = new Random();
		        int msec = Math.abs(rms.nextInt()%999);
		        String msecStr = String.valueOf(msec);
		        
		        // make sure milliseconds have three significant digits
		        if(msec<10){
		        	msecStr = "00" + msecStr;
		        }else if(msec>10 && msec<100){
		        	msecStr = "0" + msecStr;
		        }
		        
		        dateStr = dateStr.substring(0,17) + secStr + "." + msecStr +  dateStr.substring(23,dateStr.length());
		        
		    	writer.append(dateStr);
		    	writer.append(',');
		    	
		    	if(!bearing){
		    	// distance (miles)
		    	String distStr = String.valueOf(tripBeanList.get(k).getDistance());
		    	writer.append(distStr);
		    	
		    	}
		    	
		    	if(bearing){
		    	
			    	// bearing (degrees)
			    	String bearStr = String.valueOf(tripBeanList.get(k).getBearing());
			    	writer.append(bearStr);
			    	
			    	writer.append(',');
			    	
			    	// bearing (cardinal direction)
			    	String cardStr = String.valueOf(tripBeanList.get(k).getDirection());
			    	writer.append(cardStr);
		    	}
		    	
		    	// next line
		        writer.append('\n');
		    }
	 
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	}

}
