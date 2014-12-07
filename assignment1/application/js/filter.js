/*global $*/
/*jslint nomen:true*/
(function (scope) {
	'use strict';

	var provinces, cb;

	scope.filter = scope.filter || {};

	/**
	 * Set up filter
	 *
	 * @param Array[Province] _provinces The provinces
	 * @param function _cb Callback to set feature collection for screen
	 */
	scope.filter.setup = function (_provinces, _cb) {
		provinces = _provinces;
		cb = _cb;

		$("#filter")
			.empty()
			.append($("<input>")
						.addClass('col-md-12 form-control')
						.attr('type', 'search')
						.attr('placeholder', 'Start typing to filter\u2026')
						.on('keyup', scope.filter.apply));
	};

	/**
	 * Applies filter
	 */
	scope.filter.apply = function (e) {
		var val = $(e.target).val(), ret;
		if (val === '') {
			scope.filter.clear();
			return;
		}
		ret = scope.Province.getByName(provinces, val);
		if (ret.length < 1) {
			$("#filter").addClass("has-warning");
		} else {
			$("#filter").removeClass("has-warning");
		}
		cb(ret);
	};

	/**
	 * Clears filter
	 */
	scope.filter.clear = function () {
		cb(scope.Province.getCollection(provinces).features);
	};

}(this));
