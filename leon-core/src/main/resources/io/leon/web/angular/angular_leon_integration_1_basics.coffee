# ----------
# This is the first part of the angular leon integration. To keep it clear, the integration is split into several files,
# each containing code for a specific topic.
#
# Define things depending on angular only in this file! All other things like leon angular utils are not defined yet.
# The other integration steps can depend on the things defined here.
#
# Basically we define an angular module for leon. All the other services leon provides, can be registered at this
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
# expose leon browser support as angular service to enable usage via DI
#----------

#----------
# Register an unmodified version of leon as service. The unmodified service is needed by the modified version of leon
# (with angular specifics) below. We can't use the prototype / __proto__ in the modified version which is set to
# unmodified leon, because 'this' is bound to something different when a service function is called and therefor we
# can't reach the correct prototype.
#----------
leonUnmodified = ->
leonUnmodified.prototype = getLeon()
leonAngular.leonCoreModule.service("$leonUnmodified", leonUnmodified)

#----------
# Register a modified version of leon as angular service. This modified version is used for dependency injection by
# angular.
# Leon's communication between the browser and the server is asynchronous. If you tell leon to call a service
# on a server or you register for a topic, you'll have to give a callback function to leon, which is called when the
# server send data. In this callback you can proceed the server response.
# Angular is able to detect changes to the scope and even to services like $location in specific life-cycle steps
# only ($digest). The callback function you gave is called outside this life-cycle step. To enable angular to detect the
# changes made in the given callback function, we must call this function within the $apply method of a scope. The
# $apply method calls the given function, which calls the callback. So the callback is called within $apply.
# $apply calls $rootScope.$digest (see angular API documentation of $apply). So there is no difference between using
# the $apply method of a specific scope or the $apply method of $rootScope. $rootScope can be injected, so we don't need
# to carry the specific scope with us.
#----------
leonModified = ($leonUnmodified, $rootScope) ->
	@service = (url, methodName) ->
		call: (args...) ->
			params = []
			callback = args[args.length - 1];
			
			if angular.isFunction(callback)
				params = args.slice(0, args.length - 1)

				scopedCallback = (result) ->
					$rootScope.$apply ->
						callback(result)

				params.push(scopedCallback)
			else
				params = args.slice(0, args.length)

			$leonUnmodified.service(url, methodName).call.apply(this, params)

	@subscribeTopic = (topicId, handler) ->
		scopedHandler = (data) ->
			$rootScope.$apply ->
				handler(data)

		$leonUnmodified.comet.subscribeTopic(topicId, scopedHandler)

leonModified.prototype = getLeon()

leonModified.$inject = ["$leonUnmodified", "$rootScope"]
leonAngular.leonCoreModule.service("$leon", leonModified)
