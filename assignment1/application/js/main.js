(function(scope){
	scope.data = [];

	$(function(){
		var width = $("#mainContainer").width(),
		height = $("#mainContainer").width();

		var cityData = d3.map();

		var linearColorScale = d3.scale.linear()
			.domain([0.0, 100.0])
			.range(["white", "red"]);

		var svg = d3.select($("#main")[0])
			.attr("width", width)
			.attr("height", height);

		// Setup the map projection for a good depiction of The Netherlands. The
		// projection is centered on the geographical center of the country, which
		// happens to be the city of Lunteren.
		var projection = d3.geo.albers()
			.rotate([0, 0])
			.center([5.6, 52.1])
			.parallels([50, 53])
			.scale(11000)
			.translate([width/2,height/2]);

		var path = d3.geo.path().projection(projection);

		var g = svg.append("g");

		function dataLoaded(error, mapData) {
			scope.data = mapData.features;
			linearColorScale.domain([d3.min(cityData.values()), d3.max(cityData.values())]);
			g.selectAll("path")
				.data(mapData.features).enter()
				.append("path")
				.attr("d", path)
				.style("fill", function(d) {
					return linearColorScale(cityData.get(d.gm_code));
				})
				.each(function(d, i){
					$(this).data('id', d.gm_code);
					$(this).on('click', function(e){
						$(".detailExplanation").hide();
						showDetail($(this).data('id'));
					});
				})
				.append("title").text(function(d) {
					return d.gm_naam + ": " + parseInt(cityData.get(d.gm_code)) + "% 50 jaar of ouder";
				});
		}

		queue()
			.defer(d3.json, "/res/cities-geometry.json")
			.defer(d3.tsv, "/res/cities-data.txt", function(d) {
				cityData.set(d.Code, parseFloat(d.P_50_54_JR) + parseFloat(d.P_55_59_JR) + parseFloat(d.P_60_65_JR) + parseFloat(d.P_65_69_JR) + parseFloat(d.P_70_74_JR) + parseFloat(d.P_75_79_JR) + parseFloat(d.P_80_84_JR) + parseFloat(d.P_85_89_JR) + parseFloat(d.P_90_94_JR) + parseFloat(d.P_95_EO_JR));
			})
			.await(dataLoaded);

	});
}(this));
