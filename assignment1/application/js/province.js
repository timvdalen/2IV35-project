/*jslint nomen: true*/
(function (scope) {
	'use strict';
	
	/**
	 * Represents a Province on the map
	 * 
	 * @param _name String Name of the province
	 * @param _municipalities Array[Municipality] The municipalities in this province
	 * @param _feature Feature GeoJSON feature that represents the province on a map
	 */
	scope.Province = function (_name, _municipalities, _feature) {
		var
			///String Name of the Province
			name,
			///Array[Municipality] The municipalities in this Province
			municipalities,
			///Bool Whether or not this municipality is expanded
			expanded,
			///Feature The GeoJSON feature that represents the province on a map
			feature,
			///Province Reference to the current Province
			that = this;

		/**
		 * Gets the name of the Province
		 *
		 * @return String The name of the Province
		 */
		this.getName = function () {
			return name;
		};
		
		/**
		 * Expands or contracts this province
		 * 
		 * @param expand The new expansion state
		 */
		this.expand = function (expand) {
			expanded = expand;
		};
		
		/**
		 * Gets all features in this province
		 * 
		 * @return Array[Feature] GeoJSON features in the province
		 */
		this.getFeatures = function () {
			if (expanded) {
				//The province is expanded, return individual municipalities
				var ret = [], i;
				for (i = 0; i < municipalities.length; i += 1) {
					ret.push(municipalities[i].getFeature());
				}
				return ret;
			} else {
				//The province is not expanded, return its own feature
				return [feature];
			}
		};
		
		/**
		 * Gets the main value for this province
		 * 
		 * @return Float The value
		 */
		this.getValue = function () {
			var total = 0, i;
			
			for (i = 0; i < municipalities.length; i += 1) {
				total += municipalities[i].getValue();
			}
			
			return (total / municipalities.length);
		};
		
		//Constructor
		(function () {
			name = _name;
			municipalities = _municipalities;
			feature = _feature;
			feature.parent = that;
			
			expanded = false;
		}());
		
		return this;
	};
	
	/**
	 * Gets a GeoJSON feature collection for the given provinces
	 * 
	 * @param provinces Array[Province] The provinces to get the collection for
	 * @return FeatureCollection Collection containing data for all the provinces
	 */
	scope.Province.getCollection = function (provinces) {
		var features = [], i;
		for (i = 0; i < provinces.length; i += 1) {
			features.push.apply(features, provinces[i].getFeatures());
		}
		
		return {
			type: "featureCollection",
			features: features
		};
	};
	
	/**
	 * Gets all values for the given provinces, useful for calculating min and max values
	 * 
	 * @return Array[Float] All values
	 */
	scope.Province.getValues = function (provinces) {
		var ret = [], i;
		for (i = 0; i < provinces.length; i += 1) {
			ret.push(provinces[i].getValue());
		}
		return ret;
	};
	
	/**
	 * Parses a JSON representation of a Province and its Municipalities
	 * 
	 * @param data JSON representation of a Province and its Municipalities
	 * @return Province The Province
	 */
	scope.Province.fromJSON = function (data) {
		var municipalities = [], i;
		
		for (i = 0; i < data.municipalities.length; i += 1) {
			municipalities.push(scope.Municipality.fromJSON(data.municipalities[i]));
		}
		
		return new scope.Province(
			data.name,
			municipalities,
			data.feature
		);
	};
	
	/**
	 * Contracts all given Provinces
	 */
	scope.Province.contractAll = function (provinces) {
		var i;
		for (i = 0; i < provinces.length; i += 1) {
			provinces[i].expand(false);
		}
	};

}(this));
