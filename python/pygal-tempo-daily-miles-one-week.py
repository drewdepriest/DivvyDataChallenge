import pygal
import datetime
import json
from tempodb import Client
from pygal.style import BlueStyle

API_KEY = 'API_KEY'
API_SECRET = 'API_SECRET'
MILES_SERIES_KEY = 'divvy.trip.miles'

client = Client(API_KEY, API_SECRET)

start = datetime.date(2013, 9, 2)
end = start + datetime.timedelta(days=5)

data = client.read_key(MILES_SERIES_KEY, start, end, interval="1hour", function="sum")
data_list = []
x_label_list = []
x_label_major_list = []

data_count=0
for datapoint in data.data:

	if data_count%24==0:
		x_label_major_list.append(datapoint.ts.strftime("%b %e, %Y %H:%M:%S"))
	
	x_label_list.append(datapoint.ts.strftime("%b %e, %Y %H:%M:%S"))
	data_list.append(datapoint.value)
	data_count = data_count + 1

chart = pygal.Line(fill=True,x_label_rotation=40,interpolate='cubic',disable_xml_declaration=True,explicit_size=True,style=BlueStyle,show_minor_x_labels=False)
chart.title = '2013 Daily Miles Ridden on Divvy Bikes'
chart.x_labels_major = x_label_major_list
chart.x_labels = x_label_list
chart.add('Miles',data_list)
chart.render_to_file('bar_chart_tempo-daily-miles-one-week.svg')  