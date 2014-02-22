import pygal
import datetime
import json
import itertools
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
coords = zip(temperature_data_list,miles_data_list)


chart = pygal.XY(stroke=False,disable_xml_declaration=True,explicit_size=True,style=BlueStyle,x_label_rotation=40,x_title='Daily High Temperature (F)')
chart.title = '2013 Miles vs High Temperature'
chart.add('Miles',coords)
chart.render_to_file('xy_chart_tempo-daily-miles-temperature.svg')  