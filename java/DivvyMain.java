package com.drew.github;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

public class DivvyMain{

	final static ArrayList<TripBean> tbList = new ArrayList<TripBean>();
	
	 /*
	 *  USE THESE THREE VARIABLES TO DETERMINE WHETHER OR NOT TO CALL MAP API
	 *  
	 * 	(stagger them every 10,000 at a time so as to avoid timeouts)
	 *	Round 1: 0-5000
	 *	Round 2: 5000 -  10000
	 *	Round 3: 10000 - 15000
	 *	Round 4: 15000 - 20000
	 *	Round 5: 20000 - 25000 
	 *  Round 6: 25000 - 30000
	 *  Round 7: 30000 - 35000
	 *  Round 8: 35000 - 40000
	 *  Round 9: 40000 - 44180
	 *
	 */
	// ONLY leave buildMaps TRUE if you're still building the distances from MapQuest OSM
	// Once that's all good, make sure your local distance file is named Divvy_Trips_2013-DISTANCE.csv...
	// ... and set buildMaps to FALSE.
	static Boolean buildMaps = false;
	static int startCalls = 0;
	static int numCalls = 4500;
	
	// if this flag is TRUE, we will run the 8-hour cross-reference function to write mileage for all trips
	static Boolean writeMileage = false;
	
	// if this flag is TRUE, we will prep the divvy-trips-mileage-2013-FINAL-LITE.csv file for DaaS timestamps
	static Boolean prepDaaS = true;
	
	// if this flag is TRUE, we will calculate the bearing & cardinal direction of each trip
	static Boolean getBearing = false;

	public static void main(String[] args) throws ParseException, IOException {
		
		long time0 = System.currentTimeMillis();
		int step = 1;
		
		// if we're on the step of sending this to DaaS APIs, don't read in files here
		if(!(prepDaaS || getBearing)){
		// STEP 1: Read in the data from the trips CSV, build a bean for each one
			
			System.out.println("Step " + step + ": Read in data from CSV files.");
			ReadCSV.main(tbList);
			step++;
		
		// STEP 2: Remove all duplicate trips and copy remaining trips to new arraylist
		// (this will drastically reduce the number of API calls to make)
		
			System.out.println("Step " + step + ": Remove all duplicate trips.");
			ArrayList<TripBean> tbListNoDups = new ArrayList<TripBean>();
			step++;

			// Add the original list to a HashSet, which doesn't allow duplicates
			// (the hash function of the bean is configured to look only at to/from station IDs)
			HashSet<TripBean> hs = new HashSet<TripBean>();
			hs.addAll(tbList);
			
			// add the hash set (with no duplicates) to the new tbListNoDups list
			tbListNoDups.clear();
			tbListNoDups.addAll(hs);
			
			System.out.println("Number of unique trip routes: " + tbListNoDups.size());

		// STEP 3: Make calls to MapQuest OSM API to calculate distance of each trip
		// ONLY if buildMaps == TRUE
			if(buildMaps){
				
				System.out.println("Step " + step + ": Start calling maps API.");
				step++;
				
				int totalCalls = startCalls + numCalls;
				
				CallMapQuestAPI.main(tbListNoDups, 0, tbListNoDups.size());
				
				String fileName = "PATH_TO_FILENAME" + startCalls + "-" + totalCalls + ".csv";
				WriteCSV.main(tbListNoDups,fileName,false);
	
			}
			
			if(writeMileage){
					
				// STEP 4: Using original tbList (all 700,000+ trips), add distance from the noDups list
					System.out.println("Step " + step + ": Set distance for all 700,000+ trips.");
					ReadCSV.getDistanceInfo(tbList);
					step++;

				// STEP 5: Write CSV of all 700,000 trips with starttime and distance only
					System.out.println("Step " + step + ": Write the CSV files.");
					step++;
					
					// list of the individual trips, no duplicates
					String fileName = "PATH_TO_FILENAME_NO_DUPS.csv";
					WriteCSV.main(tbListNoDups,fileName,false);
					
					// FINAL MASTER LIST WITH DISTANCES
					String fileNameMaster = "PATH_TO_FILENAME-FINAL.csv";
					WriteCSV.main(tbList,fileNameMaster,false);
					
					// FINAL MASTER LIST, STARTTIME/DISTANCE ONLY
					String fileNameMasterLite = "PATH_TO_FILENAME-FINAL-LITE.csv";
					WriteCSV.main(tbList,fileNameMasterLite,true);
				
			}
		}	
		
		// STEP 7: push series found in step 3 to cloud-based DaaS for analysis
		if(prepDaaS){
			
			String csvFile = "PATH_TO_FILENAME.csv";
			ReadCSV.prepTimestamps(tbList, csvFile);
			
			String fileNameWx = "PATH_TO_FILENAME-WEATHER.txt";
			WriteCSV.makeTempo(tbList,fileNameWx,false);
			
		}
		
		if(getBearing){
			
			ReadCSV.main(tbList);
			System.out.println("Got all the trips, now to get me bearings, matey...");
			for(int a=0;a<tbList.size();a++){
				
				// if a multiple of 1000, let user know percent complete
				if(a%1000==0){
					DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					Calendar cal = Calendar.getInstance();

					float tempFloat = (float) (a)*100/(tbList.size());
					System.out.println(tempFloat + "% complete at " + dateFormat.format(cal.getTime()));
					if(tempFloat > 49.9 && tempFloat < 51.0){
						System.out.println("** ::Bon Jovi:: Whooah, we're halfway there...**");
					}
				}
				
				getHeading(tbList.get(a));
			}
			String csvFile = "PATH_TO_FILENAME-FINAL-BEARING.csv";
			WriteCSV.makeTempo(tbList, csvFile, true);
		}

		// Step 6: send email to myself as notification that everything is done
		
			System.out.println("Step " + step + ": Send email to self as notification that the job is complete.");
			SendEmail.gmail();
		  
			long time1 = System.currentTimeMillis();
			long diff = (time1-time0);
			int seconds = (int) (diff / 1000) % 60 ;
			int minutes = (int) ((diff / (1000*60)) % 60);
			System.out.println("Total time elapsed: " + minutes + " mins " + seconds + " seconds");

  }
	/*
	 * Given two pairs of lat/long, calculate direction
	 */
	public static void getHeading(TripBean tb){
		
		String directions[] = {"NE", "E", "SE", "S", "SW", "W", "NW", "N"};
		
		double lat1 = tb.getLatFrom();
		double long1 = tb.getLongFrom();
		double lat2 = tb.getLatTo();
		double long2 = tb.getLongTo();
		
		double longDiff = long2 - long1;
		
		// Haversine FTW
		double y = (Math.sin(longDiff)*Math.cos(lat2));
		double x = (Math.cos(lat1)*Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(longDiff));
		
		double bearingDeg =(Math.toDegrees(Math.atan2(y, x)));
		tb.setBearing(bearingDeg);
		
		double index = bearingDeg - 22.5;
		if(index<0){
			index += 360;
		}
		index = index/45;
		
		// return cardinal direction for that bearing
		tb.setDirection(directions[(int) index]);		
	}
	
}