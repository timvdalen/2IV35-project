var merge = require('turf-merge')
var fs = require('fs')
var zh = JSON.parse(fs.readFileSync('tmp/zuidholland_unmerged.json'))
var merged = merge(zh)
fs.writeFile("tmp/zuidholland_mergedplease.json",JSON.stringify(merged) , function(err) {
    if(err) {
        console.log(err);
    } else {
        console.log("The file was saved!");
    }
}); 


