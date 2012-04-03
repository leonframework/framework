# ----------
# This is the first part of the angular leon integration.
# Define things depending on angular only in this file! All other things like leon angular utils are not defined yet.
# The other integration steps can depend on the things defined here.
#
# Basically this is an angular module for leon.
# All services can be registered at this module and other modules can depend on it.
# ----------



@getLeon().angular = {} if !@getLeon().angular?



# create a angular module to support tight integration with angular
# customer modules can depend on this to get access to leon services
@getLeon().angular.leonModule = angular.module('leon', [])



# Basic injector for internal usage. Try to use this as less as possible.
# Expose your code as angular service in your own module and depend on leon module
# or add it as service to leon's module.
@getLeon().angular.injector = angular.injector(['ng', 'leon']) if !getLeon().angular.injector?



#----------
# expose leon browser support as angular service to enable di
#----------

# constructor function to use as service provider with angular
service = -> 
	# place to add angular specific things
	# do it like this: @aNewFunction = ...


# make everything available defined in leon browser support 
service.prototype = getLeon()

@getLeon().angular.leonModule.service "$leon", service
