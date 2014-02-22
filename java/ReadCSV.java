package com.drew.github;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ReadCSV {

	private static ArrayList<TripBean> tbList;
	private static int roundTripCount = 0;
	
	public static void main(ArrayList<TripBean> tripBeanList) throws ParseException {
		
		tbList = tripBeanList;
		ReadCSV obj = new ReadCSV();
		obj.getStationInfo();
		obj.getStationLatLong();
	}
	
	public void getStationInfo() throws ParseException {
		
		String csvFile = "Divvy_Trips_2013.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		try {
			 
			br = new BufferedReader(new FileReader(csvFile));
			
			// skip the first line (headers)
			br.readLine();
			while ((line = br.readLine()) != null) {
				
			    // use comma as separator
				String[] trip = line.split(cvsSplitBy);
				
				// if a station is undefined, skip it
				if(trip[1].equals("#N/A") || trip[2].equals("#N/A")){
					// do nothing
				}else{
				// for each trip, build a new bean
				TripBean tb = new TripBean();

				// store values in the new bean
				Date date = new SimpleDateFormat("M/dd/yyyy HH:mm", Locale.ENGLISH).parse(trip[0]);
				tb.setStartTime(date);
				tb.setStationIdFrom(Integer.valueOf(trip[1]));
				tb.setStationIdTo(Integer.valueOf(trip[2]));
			
				// add the new bean to the list
				tbList.add(tb);
				}
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
	}
	
	public void getStationLatLong() {
		
		String csvFile = "Divvy_Stations_2013.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		try {
			 		
			br = new BufferedReader(new FileReader(csvFile));
			
			// skip the first line (headers)
			br.readLine();

			// walk through each line of the CSV
			while ((line = br.readLine()) != null) {
					
				// loop through each of the trip beans
				for(int i=0;i<tbList.size();i++){
						
					// use comma as separator
					String[] station = line.split(cvsSplitBy);
					
					// if a station is undefined, skip it
					if(station[1].equals("#N/A")){
						// do nothing
					}else{
					
						// if the FROM station ID in the bean matches this station's ID, get the lat and long
						if(tbList.get(i).getStationIdFrom() == Integer.valueOf(station[1])){
							
							tbList.get(i).setLatFrom(Float.valueOf(station[2]));
							tbList.get(i).setLongFrom(Float.valueOf(station[3]));
						}
	
						// if the TO station ID in the bean matches this station's ID, get the lat and long
						if(tbList.get(i).getStationIdTo() == Integer.valueOf(station[1])){
	
							tbList.get(i).setLatTo(Float.valueOf(station[2]));
							tbList.get(i).setLongTo(Float.valueOf(station[3]));
						}
						
						// if the FROM station and the TO station are the same, it's a round trip
						if(tbList.get(i).getStationIdFrom() == Integer.valueOf(tbList.get(i).getStationIdTo())){
							tbList.get(i).setRoundTrip(true);
							
						}else{
							tbList.get(i).setRoundTrip(false);
						}
					}
				}
				
				
			}
			
			for(int n=0;n<tbList.size();n++){
				if(tbList.get(n).getRoundTrip())
				{
					roundTripCount = roundTripCount + 1;
				}
			}
			
			System.out.println("Total trips: " + tbList.size());
			System.out.println("Number of round trips: " + roundTripCount);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
	}
	
	public static void getDistanceInfo(ArrayList<TripBean> tbList) throws ParseException {
			
			String csvFile = "PATH_TO_FILENAME-DISTANCES.csv";
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";
			float totalMiles = 0;
			
			try {
		 		
				br = new BufferedReader(new FileReader(csvFile));
				
				// skip the first line (headers)
				br.readLine();
				int lineCount = 0;
				// walk through each line of the CSV
				while ((line = br.readLine()) != null) {
	
					// loop through each of the trip beans
					for(int i=0;i<tbList.size();i++){
						
						// use comma as separator
						String[] trip = line.split(cvsSplitBy);
							
							// if the FROM station and the TO station of distance file match the bean, set the distance
							// (** don't forget to check both ways - to/from AND from/to **)
							if(tbList.get(i).getStationIdFrom() == Integer.valueOf(trip[1]) && tbList.get(i).getStationIdTo() == Integer.valueOf(trip[4])
									|| tbList.get(i).getStationIdFrom() == Integer.valueOf(trip[4]) && tbList.get(i).getStationIdTo() == Integer.valueOf(trip[1])){
								tbList.get(i).setDistance(Float.valueOf(trip[7]));	
								totalMiles = totalMiles + Float.valueOf(trip[7]);
							}
							
							// if this is a round trip, set distance to 0.00
							if(tbList.get(i).getRoundTrip()){
								tbList.get(i).setDistance((float) 0.00);
								totalMiles = totalMiles;
							}
						
					}
					// if a multiple of 500, let user know percent complete
					if(lineCount%500==0){
						DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
						Calendar cal = Calendar.getInstance();

						float tempFloat = (float) (lineCount)*100/(44180);
						System.out.println(tempFloat + "% complete at " + dateFormat.format(cal.getTime()));
						if(tempFloat > 49.9 && tempFloat < 51.0){
							System.out.println("** Clowns to the left of me, jokers to the right, here I am stuck in the middle with you...**");
						}
					}
					lineCount++;
	
				}
				
				double averageMiles = totalMiles/tbList.size();
				System.out.println("Total miles traveled on Divvy bikes, June to December 2013: " + totalMiles);
				System.out.println("Average miles per trip on a Divvy bike: " + averageMiles);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		
	}
	
	public static void prepTimestamps(ArrayList<TripBean> tripBeanList, String fileName) throws ParseException{
		
		tbList = tripBeanList;
		String csvFile = fileName;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		try {
			 
			br = new BufferedReader(new FileReader(csvFile));
			
			// skip the first line (headers)
			br.readLine();
			int lineCount = 0;
			
			while ((line = br.readLine()) != null) {
				
			    // use comma as separator
				String[] trip = line.split(cvsSplitBy);
				TripBean tb = new TripBean();

				// store values in the new bean
				String tempDate = trip[0];
				
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				
				Date noaaDate = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse(tempDate);
				System.out.println(noaaDate);
		        tb.setStartTime(noaaDate);
				
				tb.setDistance(Float.parseFloat(trip[1]));
		        
		        // if a multiple of 10,000, let user know percent complete
				if(lineCount%10000==0){
					DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Calendar cal = Calendar.getInstance();

					float tempFloat = (float) (lineCount)*100/(757910);
					System.out.println(tempFloat + "% complete at " + dateFormat.format(cal.getTime()));
					if(tempFloat > 49.9 && tempFloat < 51.0){
						System.out.println("** Started from the bottom, now we're (halfway) here...**");
					}
				}
				tbList.add(tb);
		        lineCount++;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
