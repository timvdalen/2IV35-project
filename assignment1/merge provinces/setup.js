var fs = require('fs')
var merge = require('turf-merge')
var polys = JSON.parse(fs.readFileSync('cities-geometry.json'))
var zeeland = JSON.parse(fs.readFileSync('zeeland.json'))
fixProvince(zeeland,'tmp/zeeland_unmerged.json')
var nb = JSON.parse(fs.readFileSync('noordbrabant.json'))
fixProvince(nb,'tmp/noordbrabant_unmerged.json')
var zh = JSON.parse(fs.readFileSync('zuidholland.json'))
fixProvince(zh,'tmp/zuidholland_unmerged.json')
var nh = JSON.parse(fs.readFileSync('noordholland.json'))
fixProvince(nh,'tmp/noordholland_unmerged.json')
var over = JSON.parse(fs.readFileSync('overrijssel.json'))
fixProvince(over,'tmp/overrijssel_unmerged.json')
var flevo = JSON.parse(fs.readFileSync('flevoland.json'))
fixProvince(flevo,'tmp/flevoland_unmerged.json')
var gelder = JSON.parse(fs.readFileSync('gelderland.json'))
fixProvince(gelder,'tmp/gelderland_unmerged.json')
var lim = JSON.parse(fs.readFileSync('limburg.json'))
fixProvince(lim,'tmp/limburg_unmerged.json')
var gro = JSON.parse(fs.readFileSync('groningen.json'))
fixProvince(gro,'tmp/groningen_unmerged.json')
var fri = JSON.parse(fs.readFileSync('friesland.json'))
fixProvince(fri,'tmp/friesland_unmerged.json')
var dre = JSON.parse(fs.readFileSync('drenthe.json'))
fixProvince(dre,'tmp/drenthe_unmerged.json')
var utr = JSON.parse(fs.readFileSync('utrecht.json'))
fixProvince(utr,'tmp/utrecht_unmerged.json')

function fixProvince(province,province_naam){
var arr = province.gmcodes
wstream = fs.createWriteStream(province_naam);
wstream.write('{ \n "type": "FeatureCollection", \n "features": [ \n')
hippeshit2(arr,polys.features)
wstream.write(' ] \n } ')
wstream.end();
}
function hippeshit(gmcode,features,bool){
	for( i=0; i<features.length; i++){
		if(features[i].gm_code == gmcode){
			wstream.write('{ \n "type": "Feature", \n "properties": {}, \n "geometry": ')
			wstream.write(JSON.stringify(polys.features[i].geometry))
			if(bool){
				wstream.write('\n } \n')
			}
			else{
				wstream.write('\n }, \n')
			}
			
		
		}
	}
}
function hippeshit2(gmcodes,features){
	for(j=0;j<gmcodes.length;j++){
		if(j==gmcodes.length-1)
		{
		hippeshit(gmcodes[j],polys.features,true)
			}	
		else
		{
			hippeshit(gmcodes[j],polys.features,false)	
			}
	}
}

