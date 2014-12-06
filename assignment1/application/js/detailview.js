/*global $,d3*/
(function (scope) {
	'use strict';
	var groups = [
		{
			label: '0-9',
			data: ["P_00_04_JR", "P_05_09_JR"]
		},
		{
			label: '10-19',
			data: ["P_10_14_JR", "P_15_19_JR"]
		},
		{
			label: '20-29',
			data: ["P_20_24_JR", "P_25_29_JR"]
		},
		{
			label: '30-39',
			data: ["P_30_34_JR", "P_35_39_JR"]
		},
		{
			label: '40-49',
			data: ["P_40_44_JR", "P_45_49_JR"]
		},
		{
			label: '50-59',
			data: ["P_50_54_JR", "P_55_59_JR"]
		},
		{
			label: '60-69',
			data: ["P_60_65_JR", "P_65_69_JR"]
		},
		{
			label: '70-79',
			data: ["P_70_74_JR", "P_75_79_JR"]
		},
		{
			label: '80-89',
			data: ["P_80_84_JR", "P_85_89_JR"]
		},
		{
			label: '90+',
			data: ["P_90_94_JR", "P_95_EO_JR"]
		}
	];

	function translation(x, y) {
		return 'translate(' + x + ',' + y + ')';
	}

	function dataFromMunicipality(municipality) {
		var data = [], i, j, pop;

		for (i = 0; i < groups.length; i += 1) {
			pop = 0;
			for (j = 0; j < groups[i].data.length; j += 1) {
				pop += parseInt(municipality.getProperty(groups[i].data[j]), 10);
			}
			data.push({
				group: groups[i].label,
				pop: pop
			});
		}

		return data;
	}
	
	$(function () {
		var w, h, margin, regionWidth, pointA, pointB;

		w = $("#detailContainer").width();
		h = $("#detailContainer").width() * 0.8;

		// margin.middle is distance from center line to each y-axis
		margin = {
			top: 20,
			right: 20,
			bottom: 24,
			left: 20,
			middle: 28
		};
			
		// the width of each side of the chart
		regionWidth = w;

		// these are the x-coordinates of the y-axes
		pointA = 0;
		pointB = margin.middle + 5;

		scope.showDetail = function (municipality) {
			var data, totalPopulation, percentage, svg, maxValue, xScale, xScaleLeft, xScaleRight, yScale, yAxisLeft, yAxisRight, xAxisRight, xAxisLeft, leftBarGroup, rightBarGroup, bar;

			$("#detail").empty();
			$(".detailExplanation").hide();
		
			data = dataFromMunicipality(municipality);

			// GET THE TOTAL POPULATION SIZE AND CREATE A FUNCTION FOR RETURNING THE PERCENTAGE
			totalPopulation = d3.sum(data, function (d) { return d.pop; });
			percentage = function (d) { return d / totalPopulation; };
			  
			  
			// CREATE SVG
			svg = d3.select($("#detail")[0])
				.attr('width', margin.left + w + margin.right)
				.attr('height', margin.top + h + margin.bottom)
				// ADD A GROUP FOR THE SPACE WITHIN THE MARGINS
				.append('g')
					.attr('transform', translation(margin.left, margin.top));

			// find the maximum data value on either side
			//  since this will be shared by both of the x-axes
			maxValue = d3.max(data, function (d) { return percentage(d.pop); });

			// SET UP SCALES

			// the xScale goes from 0 to the width of a region
			//  it will be reversed for the left x-axis
			xScale = d3.scale.linear()
				.domain([0, maxValue])
				.range([0, regionWidth])
				.nice();

			xScaleLeft = d3.scale.linear()
				.domain([0, maxValue])
				.range([regionWidth, 0]);

			xScaleRight = d3.scale.linear()
				.domain([0, maxValue])
				.range([0, regionWidth]);

			yScale = d3.scale.ordinal()
				.domain(data.map(function (d) { return d.group; }))
				.rangeRoundBands([h, 0], 0.1);


			// SET UP AXES
			yAxisLeft = d3.svg.axis()
				.scale(yScale)
				.orient('right')
				.tickSize(0, 0);

			yAxisRight = d3.svg.axis()
				.scale(yScale)
				.orient('left')
				.tickSize(4, 0)
				.tickFormat('');

			xAxisRight = d3.svg.axis()
				.scale(xScale)
				.orient('bottom')
				.tickFormat(d3.format('%'));

			rightBarGroup = svg.append('g')
				.attr('transform', translation(pointB, 0));

			// DRAW AXES
			svg.append('g')
				.attr('class', 'axis y left')
				.attr('transform', translation(pointA, 0))
				.call(yAxisLeft)
				.selectAll('text')
				.style('text-anchor', 'middle');

			svg.append('g')
				.attr('class', 'axis y right')
				.attr('transform', translation(pointB, 0))
				.call(yAxisRight);

			svg.append('g')
				.attr('class', 'axis x right')
				.attr('transform', translation(pointB, h))
				.call(xAxisRight);

			// DRAW BARS

			bar = rightBarGroup.selectAll('.bar.right')
				.data(data)
				.enter()
				.append('g')
				.attr('class', 'barHolder');

			bar.append('rect')
						.attr('class', 'bar right')
						.attr('x', 0)
						.attr('y', function (d) { return yScale(d.group); })
						.attr('width', function (d) { return xScale(percentage(d.pop)); })
						.attr('height', yScale.rangeBand());

			bar.append('text')
				.attr('class', 'bartext')
				.attr('x', 20)
				.attr('y', function (d) { return yScale(d.group) + 23; })
				.text(function (d) { return parseInt(percentage(d.pop) * 100, 10) + "%"; });

			$("#detailContainer .municipalityName").text(municipality.getName());
		};
	});
}(this));
