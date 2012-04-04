# ----------
# This is the first part of the angular leon integration. To keep it clear, the integration is split into several files,
# each containing code for a specific topig.
#
# Define things depending on angular only in this file! All other things like leon angular utils are not defined yet.
# The other integration steps can depend on the things defined here.
#
# Basically we define an angular module for leon. All the other services leon provices, can be registered at this
# module and other modules can depend on it to use DI.
# ----------



# init
@getLeon().angular = {} if !@getLeon().angular?



# local alias which can be used as clojure to bypass this/@
leonAngular = @getLeon().angular



# create a angular module to support tight integration with angular
# customer modules can depend on this to get access to leon services
leonAngular.leonCoreModule = angular.module('leon.core', [])



#----------
# expose leon browser support as angular service to enable DI
#----------

service = -> 
	# place to add angular specific things which depends on DI
	# do it like this: @aNewFunction = ...


service.prototype = getLeon()
leonAngular.leonCoreModule.service "$leon", service
