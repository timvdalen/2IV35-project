/*global $,d3,queue*/
(function (scope) {
	'use strict';

	$(function () {
		var width, height, linearColorScale, svg, projection, path, g, provinces = [], reloadData, refreshData;

		width = $("#mainContainer").width();
		height = $("#mainContainer").width();

		linearColorScale = d3.scale.linear()
			.domain([0.0, 100.0])
			.range(["white", "red"]);

		svg = d3.select($("#main")[0])
			.attr("width", width)
			.attr("height", height);

		// Setup the map projection for a good depiction of The Netherlands. The
		// projection is centered on the geographical center of the country, which
		// happens to be the city of Lunteren.
		projection = d3.geo.albers()
			.rotate([0, 0])
			.center([5.6, 52.1])
			.parallels([50, 53])
			.scale(11000)
			.translate([width / 2, height / 2]);

		path = d3.geo.path().projection(projection);

		g = svg.append("g");

		function calculateLegend(recalculate) {
			var values, minValue, maxValue;
			values = scope.Province.getValues(provinces, recalculate);
			minValue = d3.min(values);
			maxValue = d3.max(values);
			linearColorScale.domain([minValue, maxValue]);
			$(".legendBar").css('background', '-webkit-linear-gradient(left, ' + linearColorScale(minValue) + ', ' + linearColorScale(maxValue) + ')');
			$("#legendMin").text(parseFloat(minValue/100, 10).toFixed(1));
			$("#legendMax").text(parseFloat(maxValue/100, 10).toFixed(1));
		}

		reloadData = function () {
			refreshData(scope.Province.getCollection(provinces).features);
		};

		refreshData = function (data) {
			//Clean up
			g.selectAll("path").remove();

			//Add data
			g.selectAll("path")
				.data(data).enter()
				.append("path")
				.attr("d", path)
				.style("fill", function (d) {
					return linearColorScale(d.parent.getValue());
				})
				.each(function (d, i) {
					$(this).on('click', {parent: d.parent}, function (e) {
						if (e.data.parent instanceof scope.Province) {
							//Handle Province click
							if (scope.settings.get('deexpand') === true) {
								scope.Province.contractAll(provinces);
							}
							d.parent.expand(true);
							if (scope.settings.get('rescalecolors') === true) {
								calculateLegend(true);
							}
							reloadData();
						} else if (e.data.parent instanceof scope.Municipality) {
							//Handle Municipality click
							scope.showDetail(e.data.parent);
						}
					});
				})
				.append("title").text(function (d) {
					return "Average age in " + d.parent.getName() + " is " + parseFloat(d.parent.getValue()/100).toFixed(1);
				});
		};

		function dataLoaded(error, mapData) {
			var i;
			for (i = 0; i < mapData.length; i += 1) {
				provinces.push(scope.Province.fromJSON(mapData[i]));
			}

			//Calculate color scale
			calculateLegend(false);

			reloadData();

			scope.filter.setup(provinces, function (data) {
				refreshData(data);
			});
		}

		queue()
			.defer(d3.json, "/res/data.json")
			.await(dataLoaded);

	});
}(this));
