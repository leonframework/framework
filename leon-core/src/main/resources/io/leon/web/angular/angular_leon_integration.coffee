
# create a angular module to support tight integration with angular
# customer modules can depend on this to get access to leon services
leonModule = angular.module('leon', [])
    
# expose leon browser support as angular service to enable di
leonModule.service "$leon", () ->
	leon = {}
	leon.__proto__ = getLeon()

	leon
