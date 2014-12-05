var csv = require("csv-to-json");
var fs = require('fs')
var json = csv.parse('cities-data.csv');
csv.write('cities-data.json');