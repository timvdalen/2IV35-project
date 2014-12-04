var merge = require('turf-merge')
var fs = require('fs')
var over = JSON.parse(fs.readFileSync('tmp/overrijssel_unmerged.json'))
var merged = merge(over)
fs.writeFile("tmp/overrijssel_merged.json",JSON.stringify(merged) , function(err) {
    if(err) {
        console.log(err);
    } else {
        console.log("The file was saved!");
    }
}); 
var geld = JSON.parse(fs.readFileSync('tmp/gelderland_unmerged.json'))
var merged = merge(geld)
fs.writeFile("tmp/gelderland_merged.json",JSON.stringify(merged) , function(err) {
    if(err) {
        console.log(err);
    } else {
        console.log("The file was saved!");
    }
}); 
var utr = JSON.parse(fs.readFileSync('tmp/utrecht_unmerged.json'))
var merged = merge(geld)
fs.writeFile("tmp/utrecht_merged.json",JSON.stringify(merged) , function(err) {
    if(err) {
        console.log(err);
    } else {
        console.log("The file was saved!");
    }
}); 
var nb = JSON.parse(fs.readFileSync('tmp/noordbrabant_unmerged.json'))
var merged = merge(nb)
fs.writeFile("tmp/noordbrabant_merged.json",JSON.stringify(merged) , function(err) {
    if(err) {
        console.log(err);
    } else {
        console.log("The file was saved!");
    }
}); 
var flevo = JSON.parse(fs.readFileSync('tmp/flevoland_unmerged.json'))
var merged = merge(flevo)
fs.writeFile("tmp/flevoland_merged.json",JSON.stringify(merged) , function(err) {
    if(err) {
        console.log(err);
    } else {
        console.log("The file was saved!");
    }
}); 
var nh = JSON.parse(fs.readFileSync('tmp/noordholland_unmerged.json'))
var merged = merge(nh)
fs.writeFile("tmp/noordholland_merged.json",JSON.stringify(merged) , function(err) {
    if(err) {
        console.log(err);
    } else {
        console.log("The file was saved!");
    }
}); 
var lim = JSON.parse(fs.readFileSync('tmp/limburg_unmerged.json'))
var merged = merge(lim)
fs.writeFile("tmp/limburg_merged.json",JSON.stringify(merged) , function(err) {
    if(err) {
        console.log(err);
    } else {
        console.log("The file was saved!");
    }
}); 
var zee = JSON.parse(fs.readFileSync('tmp/zeeland_unmerged.json'))
var merged = merge(zee)
fs.writeFile("tmp/zeeland_merged.json",JSON.stringify(merged) , function(err) {
    if(err) {
        console.log(err);
    } else {
        console.log("The file was saved!");
    }
}); 
var frie = JSON.parse(fs.readFileSync('tmp/friesland_unmerged.json'))
var merged = merge(geld)
fs.writeFile("tmp/friesland_merged.json",JSON.stringify(merged) , function(err) {
    if(err) {
        console.log(err);
    } else {
        console.log("The file was saved!");
    }
}); 
var gro = JSON.parse(fs.readFileSync('tmp/groningen_unmerged.json'))
var merged = merge(geld)
fs.writeFile("tmp/groningen_merged.json",JSON.stringify(merged) , function(err) {
    if(err) {
        console.log(err);
    } else {
        console.log("The file was saved!");
    }
}); 
var dre = JSON.parse(fs.readFileSync('tmp/drenthe_unmerged.json'))
var merged = merge(geld)
fs.writeFile("tmp/drenthe_merged.json",JSON.stringify(merged) , function(err) {
    if(err) {
        console.log(err);
    } else {
        console.log("The file was saved!");
    }
}); 
var zh = JSON.parse(fs.readFileSync('tmp/zuidholland_unmerged.json'))
var merged = merge(zh)
fs.writeFile("tmp/zuidholland_merged.json",JSON.stringify(merged) , function(err) {
    if(err) {
        console.log(err);
    } else {
        console.log("The file was saved!");
    }
}); 


