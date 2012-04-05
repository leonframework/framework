# ----------
# In this file everything of leon's basic crud support is defined. The functions depend on the utils defined in a
# previous step.
# Basic crud support contains a module leon.curd, controllers for list view and edit view, utils to register the needed
# crud service as well as a default implementation of the crud service.
# ----------



# local alias which can be used as clojure to bypass this/@
leonAngular = @getLeon().angular



# init
leonAngular.crud = {} if!leonAngular.crud?
leonAngular.crud.module = angular.module 'leon.crud', ['ng']



# registers the default implementation for list controller, used in the default config
leonAngular.crud.module.controller "DefaultListController", ($scope, $crudService, $leonAngularUtils, crudConfig) ->
	
	$scope.list = ->
		$crudService.list (result) ->
			$scope.model.list = result
			$scope.$digest()

	$scope.delete = (id) ->
		$crudService.delete id, (result) ->

		$scope.list()

	$scope.showEdit = (id) ->
		pathWithId = $leonAngularUtils.setRouteParameter crudConfig.editRoute.path, "id", id
		$leonAngularUtils.showRoute pathWithId 

	# init
	$scope.model = {} if !$scope.model?
	$scope.list()



# registers the default implementation for edit controller, used in the default config
leonAngular.crud.module.controller "DefaultEditController", ($scope, $crudService, $leonAngularUtils, $routeParams, crudConfig) ->
	
	$scope.delete = ->
		$crudService.delete $scope.model.current._id, (result) ->

		@showList()

	$scope.save = -> 
		$crudService.save $scope.model.current, (result) ->
			$scope.model.current = result
			$scope.$digest()

	$scope.showList = ->
		$leonAngularUtils.showRoute(crudConfig.listRoute.path)

	$scope.get = ->
		if $routeParams.id? and $routeParams.id != ""
			$crudService.get $routeParams.id, (result) ->
				$scope.model.current = result
				$scope.$digest()
		else
			$scope.model.current = $crudService.create()


	# init
	$scope.model = {} if !$scope.model?
	$scope.get()


# registers all routes of the registered crudConfig at the routeProvider
leonAngular.crud.module.config ($routeProvider, crudConfig) -> 
	routes = new Array()

	if crudConfig.listRoute?
		routes.push crudConfig.listRoute

	if crudConfig.editRoute?
		routes.push crudConfig.editRoute

	if angular.isArray(crudConfig.routes)
		routes = routes.concat crudConfig.routes

	for routeConfig in routes
		do (routeConfig) ->
			route = { template: routeConfig.template }

			if routeConfig.controller?
				route.controller = routeConfig.controller

			$routeProvider.when routeConfig.path, route			

	if crudConfig.defaultRoute?
		$routeProvider.otherwise { redirectTo: crudConfig.defaultRoute }


###
Registers the given function as crud service in leon's crud module. The curd service is used by the default
controllers DefaultListController and DefaultEditController to talk to the server.

Use this function if you want to use the default controllers but not the default implementation of curdService.
###
leonAngular.crud.registerService = (service) ->
	leonAngular.crud.module.service "$crudService", service



###
Returns the default implementation of crudService using the given serverServicePath as URL for calling an exposed sever
side service.
The default controller implementations DefaultListController and DefaultEditController uses the crudService function to 
communicate with the server. This implementation assumes the exposed methods "list", "get(id)", save(data) and
"delete(id)" on server side.
###
leonAngular.crud.createDefaultService = (serverServicePath) ->
	($leon) ->

	    @list = (callback) ->
	        $leon.service(serverServicePath, "list").call callback

	    @save = (data, callback) ->
	        $leon.service(serverServicePath, "save").call data, callback

	    @get = (id, callback) ->
	        $leon.service(serverServicePath, "get").call id, callback

	    @delete = (id, callback) ->
	        $leon.service(serverServicePath, "delete").call id, callback

	    @create = () ->
	    	{}



###
Shortcut for createDefaultService and registerService
###
leonAngular.crud.createAndRegisterDefaultService = (serverServicePath) ->
	service = leonAngular.crud.createDefaultService serverServicePath
	leonAngular.crud.registerService service



###
Returns an object containing leon.crud's default configuration. Creating an object like this and setting it via
setConfig enables you e.g. to use the default crudService but your own controllers. You can also use different
templates but all other default stuff.
You can also call this method to get the default and only modify the parts you want to.

Don't forget to set the config via setConfig if you call this method directly!
###
leonAngular.crud.createDefaultConfig = (pathPrefix) ->
	config = {}

	listPath = leonAngular.utils.assemblePath "/", pathPrefix, '/list'
	editPath = leonAngular.utils.assemblePath "/", pathPrefix, '/edit/:id'

	config.defaultRoute = listPath

	config.listRoute = { path: listPath, template: "partials/list.html", controller: "DefaultListController"}
	config.editRoute = { path: editPath, template: "partials/edit.html", controller: "DefaultEditController"}

	config



###
Registers the given config object for leon's crud support. Default implementations rely on this configuration. Only if
you don't use any default implementation, you can omit setting this configuration. See createDefaultConfig for an
example who the config should look like.
###
leonAngular.crud.setConfig = (config) ->
	leonAngular.crud.module.constant "crudConfig", config



###
Shortcut for createDefaultConfig and setConfig
###
leonAngular.crud.useDefaultConfig = ->
	config = leonAngular.crud.createDefaultConfig()
	leonAngular.crud.setConfig config



###
The ultra-shortcut: configures the hole leon crud support with only one function call (use all default implementations).
Only the URL of the server side exposed service have to be given.

See createAndRegisterDefaultService and useDefaultConfig for more information.
###
leonAngular.crud.useDefaultConfigWithDefaultService = (serverServicePath) ->
	leonAngular.crud.createAndRegisterDefaultService serverServicePath
	leonAngular.crud.useDefaultConfig()



# ----------
# expose leon angular crud support as angular service to enable DI
# ----------

# constructor function to use as service provider with angular
crudService = ->
	# place to add angular specific things 
	# do it like this: @aNewFunction = ...


# make everything available defined in loen's angular crud support
crudService.prototype = leonAngular.crud

leonAngular.crud.module.service "$leonAngularCrud", crudService
