import pygal
import datetime
import json
import itertools
from tempodb import Client
from pygal.style import BlueStyle

API_KEY = 'API_KEY'
API_SECRET = 'API_SECRET'
MILES_SERIES_KEY = 'divvy.trip.miles'
PRECIP_SERIES_KEY = 'divvy.trip.precip'

client = Client(API_KEY, API_SECRET)

start = datetime.date(2013, 6, 27)
end = start + datetime.timedelta(days=187)

miles_data = client.read_key(MILES_SERIES_KEY, start, end, interval="1day", function="sum")
miles_data_list = []
miles_data_count=0

for datapoint in miles_data.data:

	miles_data_list.append(datapoint.value)
	miles_data_count = miles_data_count + 1

# now do the same thing for precip
precip_data = client.read_key(PRECIP_SERIES_KEY, start, end)
precip_data_list = []
precip_data_count=0

for datapoint in precip_data.data:

	precip_data_list.append(datapoint.value)
	precip_data_count = precip_data_count + 1

# finally, zip the two lists to form coordinate pairs
coords = zip(precip_data_list,miles_data_list)


chart = pygal.XY(stroke=False,disable_xml_declaration=True,explicit_size=True,style=BlueStyle,x_label_rotation=40,x_title='Daily Precipitation (inches)')
chart.title = '2013 Miles vs Precipitation'
chart.add('Miles',coords)
chart.render_to_file('xy_chart_tempo-daily-miles-precip.svg')  