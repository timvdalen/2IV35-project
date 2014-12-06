/*global $*/
(function (scope) {
	'use strict';

	var settings, makeInterface, settingChanged;

	scope.settings = scope.settings || {};

	settings = {
		rescalecolors: {
			value: false,
			type: Boolean,
			description: "Rescale colors after expanding a province",
			title: "Turning this on will make it easier to spot differences, but the scale will change often"
		},
		deexpand: {
			value: true,
			type: Boolean,
			description: "Automatically de-expand other pronvinces",
			title: "If you want to compare municipalities in different provinces, turn this off"
		}
	};

	/**
	 * Gets a setting key
	 *
	 * @param key String The setting to get
	 * @return Object The value, or undefined when it isn't defined
	 */
	scope.settings.get = function (key) {
		if (settings.hasOwnProperty(key)) {
			return settings[key].value;
		} else {
			return undefined;
		}
	};

	/**
	 * Sets a setting
	 *
	 * @param key String The setting
	 * @param value Object The value
	 */
	scope.settings.set = function (key, value) {
		if (settings.hasOwnProperty(key)) {
			settings[key].value = value;
		}
	};

	makeInterface = function () {
		//Add interface item for each setting
		var form, setting, group, control;

		form = $("<form>").addClass('form-horizontal');

		for (setting in settings) {
			if (settings.hasOwnProperty(setting)) {
				//Add property to form
				group = $("<div>").addClass('form-group');

				group.append($("<label>")
							  .text(settings[setting].description)
							  .attr('for', 'setting-' + setting)
							  .attr('title', settings[setting].title)
							  .addClass("col-sm-6 control-label"));

				control = $("<div>").addClass("col-sm-6");

				switch (settings[setting].type) {
				case Boolean:
					control.append($("<input>")
									.attr("type", "checkbox")
									.on('switchChange.bootstrapSwitch', {setting: setting}, settingChanged)
								    .prop('checked', settings[setting].value));
					break;
				}

				group.append(control);
				form.append(group);
			}
		}

		$(".settings").append(form);
	};

	settingChanged = function (e, state) {
		if (settings[e.data.setting].type === Boolean) {
			scope.settings.set(e.data.setting, state);
		} else {
			scope.settings.set(e.data.setting, $(e.target).val());
		}
	};

	(function () {
		$(function () {
			makeInterface();

			$(".settings input[type=checkbox]").bootstrapSwitch();
		});
	}());
}(this));
