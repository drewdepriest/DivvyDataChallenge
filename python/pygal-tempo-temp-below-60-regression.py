import pygal
import datetime
import json
import math
from tempodb import Client
from pygal.style import BlueStyle

API_KEY = 'API_KEY'
API_SECRET = 'API_SECRET'
MILES_SERIES_KEY = 'divvy.trip.miles'
TEMPERATURE_SERIES_KEY = 'divvy.trip.temperature'

client = Client(API_KEY, API_SECRET)

start = datetime.date(2013, 6, 27)
end = start + datetime.timedelta(days=187)

miles_data = client.read_key(MILES_SERIES_KEY, start, end, interval="1day", function="sum")
miles_data_list = []
miles_data_count=0

for datapoint in miles_data.data:

	miles_data_list.append(datapoint.value)
	miles_data_count = miles_data_count + 1

# now do the same thing for the high temperature
temperature_data = client.read_key(TEMPERATURE_SERIES_KEY, start, end)
temperature_data_list = []
temperature_data_count=0

for datapoint in temperature_data.data:

	temperature_data_list.append(datapoint.value)
	temperature_data_count = temperature_data_count + 1

# finally, zip the two lists to form coordinate pairs
coords_pre = zip(temperature_data_list,miles_data_list)

coords = [[x,y] for x,y in coords_pre if not (x>60)]

# now for the regression
sum_x=0
sum_x2=0
sum_y=0
sum_y2=0
sum_ylin=0
sum_y2=0
sum_y2lin=0
sum_xy=0
sum_xylin=0
n = 0

for x,y in coords:
	sum_x = sum_x + x
	sum_x2 = sum_x2 + (x**2)

	# exponential
	sum_y = sum_y + math.log10(y)
	sum_y2 = sum_y2 + (math.log10(y))**2
	sum_xy = sum_xy + x*(math.log10(y))

	# linear
	sum_ylin = sum_ylin + y
	sum_y2lin = sum_y2lin + (y**2)
	sum_xylin = sum_xylin + x*y
	n = n+1

# calculate regression coefficient 'b' - exponential
b = 0
b = ((n*sum_xy) - (sum_x*sum_y))/(n*sum_x2 - (sum_x*sum_x))

# calculate regression coefficient 'b' - linear
b_lin = 0
b_lin = ((n*sum_xylin) - (sum_x*sum_ylin))/(n*sum_x2 - (sum_x*sum_x))

# calculate regression coefficient 'a' - exponential
a = 0
a = math.e**((sum_y - (b*sum_x))/n)

# calculate regression coefficient 'a' - linear
a_lin = 0
a_lin = (sum_ylin*sum_x2 - sum_x*sum_xylin)/(n*sum_x2 - sum_x*sum_x)

# calculate coefficient of determination (R2) - exponential
c = 0
d = 0
r = 0
c = (b)*(sum_xy - sum_x*sum_y/n)
d = sum_y2 - (sum_y*sum_y)/n
r = c/d

# calculate coefficient of determination (R2) - linear
c_lin = 0
d_lin = 0
r_lin = 0
c_lin = (b_lin)*(sum_xylin - sum_x*sum_ylin/n)
d_lin = sum_y2lin - (sum_ylin*sum_ylin)/n
r_lin = c_lin/d_lin

# calculate coefficient of correlation - exponential
p = 0
if(r>0):
	p = math.sqrt(r)
else:
	p = 0

# calculate coefficient of correlation - linear
p_lin = 0
if(r_lin>0):
	p_lin = math.sqrt(r_lin)
else:
	p_lin = 0

#calculate standard error (total variance - y variance)/(n-2) - exponential
std_err = 0
std_err = math.sqrt((d-c)/(n-2))

#calculate standard error (total variance - y variance)/(n-2) - linear
std_err_lin = 0
std_err_lin = math.sqrt((d_lin-c_lin)/(n-2))

# print both models
# exponential
print "Divvy Bike Daily Mileage vs. Daily Observed High Temperature < 60 (F)"
print "Exponential regression model for n equals " + repr(n) + ":"
print "y = " + repr(a) + "*(e^(" + repr(b) + "*x))"
print "R-square value equals " + repr(r)
print "Correlation equals " + repr(p)
print "Standard Error equals " + repr(std_err)
print "********"

# linear
print "Linear regression model for n equals " + repr(n) + ":"
print "y = " + repr(b_lin) + "x + " + repr(a_lin)
print "R-square value equals " + repr(r_lin)
print "Correlation equals " + repr(p_lin)
print "Standard Error equals " + repr(std_err_lin)

