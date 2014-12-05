/*global $,d3,queue*/
(function (scope) {
	'use strict';

	scope.data = [];

	$(function () {
		var width, height, linearColorScale, svg, projection, path, g;

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

		function dataLoaded(error, mapData) {
			var provinces = [], i, values;
			for (i = 0; i < mapData.length; i += 1) {
				provinces.push(scope.Province.fromJSON(mapData[i]));
			}

			scope.data = scope.Province.getCollection(provinces).features;

			values = scope.Province.getValues(provinces);

			linearColorScale.domain([d3.min(values), d3.max(values)]);
			g.selectAll("path")
				.data(scope.data).enter()
				.append("path")
				.attr("d", path)
				.style("fill", function (d) {
					return linearColorScale(d.parent.getValue());
				})
				.each(function (d, i) {
					$(this).on('click', {parent: d.parent}, function (e) {
						if (e.data.parent instanceof scope.Province) {
							//Handle Province click
						} else if (e.data.parent instanceof scope.Municipality) {
							//Handle Municipality click
						}
					});
				})
				.append("title").text(function (d) {
					return d.parent.getName() + ": " + parseInt(d.parent.getValue(), 10) + "% 50 jaar of ouder";
				});
		}

		queue()
			.defer(d3.json, "/res/data.json")
			.await(dataLoaded);

	});
}(this));
