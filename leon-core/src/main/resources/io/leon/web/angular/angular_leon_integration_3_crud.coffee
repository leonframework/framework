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



# registers the default implementition for list controller, used in the default config
leonAngular.crud.module.controller "DefaultListController", ($scope, $crudService, $leonAngularUtils) ->
	
	$scope.list = ->
		$crudService.list (result) ->
			$scope.model.list = result
			$scope.$digest()

	$scope.delete = (id) ->
		$crudService.delete id, (result) ->

		$scope.list()

	$scope.showEdit = (id) ->
		if id?
			$leonAngularUtils.showRoute('/edit/' + id)
		else
			$leonAngularUtils.showRoute('/edit/')

	# init
	$scope.model = {} if !$scope.model?
	$scope.list()



# registers the default implementition for edit controller, used in the default config
leonAngular.crud.module.controller "DefaultEditController", ($scope, $crudService, $leonAngularUtils, $routeParams) ->
	
	$scope.delete = ->
		$crudService.delete $scope.model.current._id, (result) ->

		@showList()

	$scope.save = -> 
		$crudService.save $scope.model.current, (result) ->
			$scope.model.current = result
			$scope.$digest()

	$scope.showList = ->
		$leonAngularUtils.showRoute("/list")

	$scope.get = ->
		if $routeParams.id?
			$crudService.get $routeParams.id, (result) ->
				$scope.model.current = result
				$scope.$digest()
		else
			$scope.model.current = $crudService.create()


	# init
	$scope.model = {} if !$scope.model?
	$scope.get()


# 
leonAngular.crud.module.config ($routeProvider, crudConfig) -> 
	if angular.isArray(crudConfig.routes)
		for routeConfig in crudConfig.routes
			do (routeConfig) ->
				route = { template: routeConfig.template }

				if routeConfig.controller?
					route.controller = routeConfig.controller

				$routeProvider.when routeConfig.path, route			

	if crudConfig.defaultRoute?
		$routeProvider.otherwise { redirectTo: crudConfig.defaultRoute }


###
TODO: comment
###
leonAngular.crud.registerService = (service) ->
	leonAngular.crud.module.service "$crudService", service



###
TODO: comment
###
leonAngular.crud.createDefaultService = (serverServicePath) ->
	($leon) ->

	    @list = (callback) ->
	        $leon.service(serverServicePath, "list").call callback

	    @save = (data, callback) ->
	        $leon.service(serverServicePath, "save").call data, callback

	    @get = (id, callback) ->
	        $leon.service(serverServicePath, "edit").call id, callback

	    @delete = (id, callback) ->
	        $leon.service(serverServicePath, "delete").call id, callback

	    @create = () ->
	    	{}



###
TODO: comment
###
leonAngular.crud.createAndRegisterDefaultService = (serverServicePath) ->
	service = leonAngular.crud.createDefaultService serverServicePath
	leonAngular.crud.registerService service


leonAngular.crud.createDefaultConfig = ->
	config = {}

	config.defaultRoute = '/list'

	config.routes = [
		{ path: "/list", template: "partials/list.html", controller: "DefaultListController"}
		{ path: "/edit/:id", template: "partials/edit.html", controller: "DefaultEditController"}
	]

	config


leonAngular.crud.setConfig = (config) ->
	leonAngular.crud.module.constant "crudConfig", config



leonAngular.crud.useDefaultConfig = ->
	config = leonAngular.crud.createDefaultConfig()
	leonAngular.crud.setConfig config



leonAngular.crud.useDefaultConfigWithDefaultService = (serverServicePath) ->
	leonAngular.crud.createAndRegisterDefaultService serverServicePath
	leonAngular.crud.useDefaultConfig()


# ----------
# expose leon angular crud support as angular service to enable di
# ----------

# constructor function to use as service provider with angular
crudService = ->
	# place to add angular specific things 
	# do it like this: @aNewFunction = ...


# make everything available defined in loen's angular crud support
crudService.prototype = leonAngular.crud

leonAngular.leonModule.service "$leonAngularCrud", crudService
