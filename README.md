DivvyDataChallenge
==================
In February 2014, Chicago's bike sharing program, Divvy, recently published trip data for the 750,000 trips taken in 2013. They're looking to use data to answer burning questions like Where are riders going? When are they going there? How far do they ride? What are top stations? What interesting usage patterns emerge? What can the data reveal about how Chicago gets around on Divvy?

You can read more at http://www.drewdepriest.com/how-to-make-sense-of-750000-bicycle-trips and see the final analysis results at http://www.drewdepriest.com/divvy.

Sample console output for running MapQuest Directions API calls:

```Step 1: Read in data from CSV files.\n
Total trips: 757911\n
Number of round trips: 41781\n
Step 2: Remove all duplicate trips.\n
Number of unique trip routes: 44180\n
Step 3: Start calling maps API.\n
0.0% complete at 02/16/2014 20:38:44\n
10.0% complete at 02/16/2014 20:45:05\n
20.0% complete at 02/16/2014 20:51:32\n
30.0% complete at 02/16/2014 20:57:55\n
40.0% complete at 02/16/2014 21:05:05\n
50.0% complete at 02/16/2014 21:11:29\n
** It just takes some time, little girl, you're in the middle, of the ride...**\n
60.0% complete at 02/16/2014 21:17:35\n
70.0% complete at 02/16/2014 21:24:28\n
80.0% complete at 02/16/2014 21:32:29\n
90.0% complete at 02/16/2014 21:38:46\n
Wrote CSV file to /Users/drewdepriest/Desktop/Divvy/divvy-trips-mileage-2013-DISTANCE-30000-35000.csv\n
Step 4: Send email to self as notification that the job is complete.\n
Done```
