var fs = require('fs')
var citydata = JSON.parse(fs.readFileSync('cities-data.json'))
var geo = JSON.parse(fs.readFileSync('cities-geometry.json'))
var zee = JSON.parse(fs.readFileSync('zeeland.json'))
var nbr = JSON.parse(fs.readFileSync('noordbrabant.json'))
var zho = JSON.parse(fs.readFileSync('zuidholland.json'))
var nho = JSON.parse(fs.readFileSync('noordholland.json'))
var ove = JSON.parse(fs.readFileSync('overrijssel.json'))
var fle = JSON.parse(fs.readFileSync('flevoland.json'))
var gel = JSON.parse(fs.readFileSync('gelderland.json'))
var lim = JSON.parse(fs.readFileSync('limburg.json'))
var gro = JSON.parse(fs.readFileSync('groningen.json'))
var fri = JSON.parse(fs.readFileSync('friesland.json'))
var dre = JSON.parse(fs.readFileSync('drenthe.json'))
var utr = JSON.parse(fs.readFileSync('utrecht.json'))
var zee2 = JSON.parse(fs.readFileSync('tmp/zeeland_merged.json'))
var nbr2 = JSON.parse(fs.readFileSync('tmp/noordbrabant_merged.json'))
var zho2 = JSON.parse(fs.readFileSync('tmp/zuidholland_merged.json'))
var nho2 = JSON.parse(fs.readFileSync('tmp/noordholland_merged.json'))
var ove2 = JSON.parse(fs.readFileSync('tmp/overrijssel_merged.json'))
var fle2 = JSON.parse(fs.readFileSync('tmp/flevoland_merged.json'))
var gel2 = JSON.parse(fs.readFileSync('tmp/gelderland_merged.json'))
var lim2 = JSON.parse(fs.readFileSync('tmp/limburg_merged.json'))
var gro2 = JSON.parse(fs.readFileSync('tmp/groningen_merged.json'))
var fri2 = JSON.parse(fs.readFileSync('tmp/friesland_merged.json'))
var dre2 = JSON.parse(fs.readFileSync('tmp/drenthe_merged.json'))
var utr2 = JSON.parse(fs.readFileSync('tmp/utrecht_merged.json'))


wstream = fs.createWriteStream('Hippeshit.json');
wstream.write(' [ \n  ')
fixAddProv(dre,dre2,'"Drenthe"')
fixAddProv(nbr,nbr2,'"Noord-Brabant"')
fixAddProv(zho,zho2,'"Zuid-Holland"')
fixAddProv(nho,nho2,'"Noord-Holland"')
fixAddProv(gro,gro2,'"Groningen"')
fixAddProv(lim,lim2,'"Limburg"')
fixAddProv(fri,fri2,'"Friesland"')
fixAddProv(fle,fle2,'"Flevoland"')
fixAddProv(ove,ove2,'"Overrijssel"')
fixAddProv(gel,gel2,'"Gelderland"')
fixAddProv(utr,utr2,'"Utrecht"')
fixAddProv(zee,zee2,'"Zeeland"')
wstream.write('\n ]   ')
wstream.end();wstream.end()

function fixAddProv(prov,prov2,naam){
wstream.write('{ \n ')
wstream.write('"name":'+naam+',\n')
wstream.write('"feature":'+JSON.stringify(prov2)+',\n')
wstream.write('"municipalities": [\n ')
	for(i=0;i<prov.gmcodes.length;i++){
		for(j=0;j<geo.features.length;j++){
			if(prov.gmcodes[i] == geo.features[j].gm_code){
				wstream.write('{ \n ')
				wstream.write('"type": "Feature", \n ')
				wstream.write('"gm_naam":"'+geo.features[j].gm_naam+'",\n')
				wstream.write('"gm_code":"'+geo.features[j].gm_code+'",\n')
				wstream.write('"geometry":'+JSON.stringify(geo.features[j].geometry)+',\n')
				wstream.write('"properties":'+JSON.stringify(geo.features[j].properties)+',\n')
			}
		}
		for(j=0;j<citydata.features.length;j++){
			if(prov.gmcodes[i] == citydata.features[j].Code){
			wstream.write('"data":'+JSON.stringify(citydata.features[j])+'\n')
		}
		
	}
	if(i==prov.gmcodes.length-1){
		wstream.write('} \n ')
	}
	else{
		wstream.write('}, \n ')
	}
}
wstream.write('\n ]}   ')
	if(naam != '"Zeeland"'){
		wstream.write(', \n ')
	}
}