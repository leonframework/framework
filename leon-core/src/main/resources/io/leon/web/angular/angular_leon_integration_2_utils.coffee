# ----------
# In this file util functions are defined. This functions can be accessed by calling @getLeon().angular....
# The util functions include things like creating controllers
# ----------



# local alias which can be used as clojure to bypass this/@
leonAngular = @getLeon().angular


# init
leonAngular.utils = {} if !leonAngular.utils?



###
Use this function instead of getLeon() to get a modified leon object
if you want the given scope to be updated after

- a service call, more accurate: after the given callback was applied
  leon.service(serviceUrl, methodName).call(callback)
- a comet message was received, more accurate: after the given handler was applied
  leon.subscribeTopic(topicId, handler)
###
leonAngular.utils.createScopedLeon = (scope, leon) ->
	scopedLeon = {}
	scopedLeon.__proto__ = leon
	
	# override service function of prototype to enable refreshing the scope
	scopedLeon.service = (url, methodName) ->
		call: (args...) ->
			refreshHook = () ->
				scope.$digest()
			leon.service(url, methodName, refreshHook).call.apply(this, args)

	# override subscribeTopic function of prototype to enable refreshing the scope
	scopedLeon.subscribeTopic = (topicId, handler) ->
		scopedHandler = (data) ->
			handler(data)
			scope.$digest()
		
		leon.comet.subscribeTopic topicId, scopedHandler

	scopedLeon



###
Creates an angular controller with some basic stuff like scoped leon, location service and model object.
The controller will also contains everything defined in the given function.

Nothing special here, it's only a little helper to reduce boilerplate code. If you need more services injected to
your controller, copy the inner function without the controller.apply and extend it!
###
leonAngular.utils.createController = (controller) ->
	($scope, $location, $leon) ->
	 
		# --- services ---
		
		$scope.location = $location
		$scope.leon = leonAngular.utils.createScopedLeon($scope, $leon)
		
		# --- state ---
		
		$scope.model = {}

		# --- user controller ---

		controller.apply($scope)


# ----------
# expose leon's angular utils as angular service to enable DI
# ----------

utilsService = ($location) ->
	# place to add angular specific things which depends on DI
	# do it like this: @aNewFunction = ...

	@showRoute = (segment) ->
		$location.path(segment)


utilsService.prototype = leonAngular.utils
leonAngular.leonCoreModule.service "$leonAngularUtils", utilsService
