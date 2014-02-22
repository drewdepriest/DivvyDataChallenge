package com.drew.github;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class CallMapQuestAPI {

	/**
	 * @param args
	 */
	private static String apiKey = "API_KEY_FROM_MAPQUEST";
	private static float totalMiles = 0;
	
	/**
	 * @param args
	 * @throws ParseException 
	 * @throws XPathExpressionException 
	 * @throws MalformedURLException 
	 */
	public static void main(ArrayList<TripBean> tripBeanList, int startCalls, int totalCalls) throws ParseException  {

		for(int j=startCalls;j<totalCalls;j++){
			
			// if a multiple of 500, let user know percent complete
			if(j%500==0){
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				Calendar cal = Calendar.getInstance();

				float tempFloat = (float) (j-startCalls)*100/(totalCalls-startCalls);
				System.out.println(tempFloat + "% complete at " + dateFormat.format(cal.getTime()));
				if(tempFloat > 49.9 && tempFloat < 51.0){
					System.out.println("** It just takes some time, little girl, you're in the middle, of the ride...**");
				}
			}
			// if this was a round trip, no need to check distance (it's 0!)
			if(tripBeanList.get(j).getRoundTrip()){
				
				// just set this trip equal to zero miles
				tripBeanList.get(j).setDistance((float) 0.0);
				
			}else{
				goMapQuest(tripBeanList.get(j));
			}
		}
	}
	
	public static void goMapQuest(TripBean tb) throws ParseException {
	   
		try {

	        String urlStr = "http://open.mapquestapi.com/directions/v2/route?key=" + apiKey + "&ambiguities=ignore&callback=renderNarrative&outFormat=xml&routeType=bicycle";
	        
	        // add the origin lat/long coords
	        urlStr += "&from=" + tb.getLatFrom() + "," + tb.getLongFrom();
	        
	        // add the destination lat/long coords
	        urlStr += "&to=" + tb.getLatTo() + "," + tb.getLongTo();
	
	        URL url = new URL(urlStr);
	        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	        connection.setRequestMethod("GET");
	        connection.setDoOutput(true);
	        connection.connect();
	        
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(new URL(urlStr).openStream());
	        
	        // use xPath to easily find the specific tag we need from the response
	        XPath xPath =  XPathFactory.newInstance().newXPath();
	        String distValue = "response/route/distance";
        	NodeList distanceListVal = (NodeList) xPath.compile(distValue).evaluate(doc, XPathConstants.NODESET);

	        for (int k = 0; k < distanceListVal.getLength(); k++) {

	        	Float miles = (float) 0.0;
	        	miles = Float.parseFloat(distanceListVal.item(k).getFirstChild().getNodeValue());

	        	tb.setDistance(miles);

	  	  	}
			
	        connection.disconnect();

	      } catch (IOException e) {
	        System.out.println(e.getMessage());
	      } catch (Throwable t) {
	        t.printStackTrace();
	      }
	}
}
