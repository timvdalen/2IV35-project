/*jslint nomen: true*/
(function (scope) {
	'use strict';
	
	/**
	 * Represents a Municipality on the map
	 *
	 * @param _name String The name of this municipality
	 * @param _code String The code of this municipality
	 * @param _feature Feature GeoJSON Feature representating this municipality on a map
	 * @param _properties Object Properties for this municipality
	 */
	scope.Municipality = function (_name, _code, _feature, _properties) {
		var
			///String The name of this municipality
			name,
			///String The code of this municipality
			code,
			///Feature GeoJSON Feature representating this municipality on a map
			feature,
			///Object Properties for this municipality
			properties,
			///Municipality Reference to the current Municipality
			that = this;
		
		/**
		 * Gets the name of the Municipality
		 *
		 * @return String The name of the Municipality
		 */
		this.getName = function () {
			return name;
		};

		/**
		 * Returns the feature that represents this municipality on a map
		 *
		 * @return Feature GeoJSON feature that represents this municipality on a map
		 */
		this.getFeature = function () {
			return feature;
		};
		
		/**
		 * Gets a property for this municipality, if it exists
		 *
		 * @param String prop The property
		 * @return Object The value of the requested property, or null if it doesn't exist
		 */
		this.getProperty = function (prop) {
			if (properties.hasOwnProperty(prop)) {
				return properties[prop];
			} else {
				return null;
			}
		};

		/**
		 * Gets the map value for this municipality
		 */
		this.getValue = function () {
			var properties = [
				"P_00_04_JR",
				"P_05_09_JR",
				"P_10_14_JR",
				"P_15_19_JR",
				"P_20_24_JR",
				"P_25_29_JR",
				"P_30_34_JR",
				"P_35_39_JR",
				"P_40_44_JR",
				"P_45_49_JR",
				"P_50_54_JR",
				"P_55_59_JR",
				"P_60_65_JR",
				"P_65_69_JR",
				"P_70_74_JR",
				"P_75_79_JR",
				"P_80_84_JR",
				"P_85_89_JR",
				"P_90_94_JR",
				"P_95_EO_JR"
			], that = this;

			return properties.reduce(function (previousValue, currentValue, index, array) {
				return previousValue + parseFloat(that.getProperty(currentValue) * ((index + 1) * 5 - 2.5));
			}, 0);
		};
		
		//Constructor
		(function () {
			name = _name;
			code = _code;
			feature = _feature;
			feature.parent = that;
			properties = _properties;
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
		return new scope.Municipality(data.gm_naam, data.gm_code, data, data.data);
	};
	
}(this));
