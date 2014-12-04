/*jslint nomen: true*/
(function (scope) {
	'use strict';
	
	scope.Municipality = function (_name, _code, _feature) {
		var
			///String The name of this municipality
			name,
			///String The code of this municipality
			code,
			///Feature GeoJSON Feature representating this municipality on a map
			feature;
		
		this.getFeature = function () {
			return feature;
		};
		
		this.getValue = function () {
			return 0.5;
		};
		
		//Constructor
		(function () {
			name = _name;
			code = _code;
			feature = _feature;
		}());
		
		return this;
	};
	
	/**
	 * Parses a JSON representation of a Municipality
	 * 
	 * @param data JSON representation of a Municipality
	 * @return Municipality The Municipality
	 */
	scope.Municipality.fromJSON = function (data) {
		return new scope.Municipality(data.gm_naam, data.gm_code, data);
	};
	
}(this));
