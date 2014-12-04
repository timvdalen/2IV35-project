var fs = require('fs')
var merge = require('turf-merge')
var polys = JSON.parse(fs.readFileSync('cities-geometry.json'))
var zeeland = JSON.parse(fs.readFileSync('zeeland.json'))
var nb = JSON.parse(fs.readFileSync('noordbrabant.json'))
var zh = JSON.parse(fs.readFileSync('zuidholland.json'))
var nh = JSON.parse(fs.readFileSync('noordholland.json'))
var over = JSON.parse(fs.readFileSync('overrijssel.json'))
var flevo = JSON.parse(fs.readFileSync('flevoland.json'))
var gelder = JSON.parse(fs.readFileSync('gelderland.json'))
var lim = JSON.parse(fs.readFileSync('limburg.json'))
var gro = JSON.parse(fs.readFileSync('groningen.json'))
var fri = JSON.parse(fs.readFileSync('friesland.json'))
var dre = JSON.parse(fs.readFileSync('drenthe.json'))
var utr = JSON.parse(fs.readFileSync('utrecht.json'))


for( i=0; i<polys.features.length; i++){
var bool = false
	for(j=0;j<nb.gmcodes.length;j++){
		if(polys.features[i].gm_code == nb.gmcodes[j]){
			bool = true
		}
	}
	for(j=0;j<zh.gmcodes.length;j++){
		if(polys.features[i].gm_code == zh.gmcodes[j]){
			bool = true
		}
	}
	for(j=0;j<nh.gmcodes.length;j++){
		if(polys.features[i].gm_code == nh.gmcodes[j]){
			bool = true
		}
	}
	for(j=0;j<over.gmcodes.length;j++){
		if(polys.features[i].gm_code == over.gmcodes[j]){
			bool = true
		}
	}
	for(j=0;j<flevo.gmcodes.length;j++){
		if(polys.features[i].gm_code == flevo.gmcodes[j]){
			bool = true
		}
	}
	for(j=0;j<gelder.gmcodes.length;j++){
		if(polys.features[i].gm_code == gelder.gmcodes[j]){
			bool = true
		}
	}
	for(j=0;j<utr.gmcodes.length;j++){
		if(polys.features[i].gm_code == utr.gmcodes[j]){
			bool = true
		}
	}
	for(j=0;j<dre.gmcodes.length;j++){
		if(polys.features[i].gm_code == dre.gmcodes[j]){
			bool = true
		}
	}
	for(j=0;j<lim.gmcodes.length;j++){
		if(polys.features[i].gm_code == lim.gmcodes[j]){
			bool = true
		}
	}
	for(j=0;j<zeeland.gmcodes.length;j++){
		if(polys.features[i].gm_code == zeeland.gmcodes[j]){
			bool = true
		}
	}
	for(j=0;j<gro.gmcodes.length;j++){
		if(polys.features[i].gm_code == gro.gmcodes[j]){
			bool = true
		}
	}
	for(j=0;j<fri.gmcodes.length;j++){
		if(polys.features[i].gm_code == fri.gmcodes[j]){
			bool = true
		}
	}
	if(bool == false){
		console.log(polys.features[i].gm_code)
		console.log(polys.features[i].gm_naam)
	}
}

