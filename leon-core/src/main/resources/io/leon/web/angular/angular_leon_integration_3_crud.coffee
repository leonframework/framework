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



###
TODO: comment
###
leonAngular.crud.createDefaultListController = (serverServicePath, editRoutePath) ->
	($scope, $leon, $leonAngularUtils) ->
		$scope.leon = $leonAngularUtils.createScopedLeon($scope, $leon) if !$scope.leon?

		$scope.list = ->
			$scope.leon.service(serverServicePath, "list").call (result) ->
				$scope.model.list = result

		$scope.delete = (id) ->
			$scope.leon.service(serverServicePath, "delete").call id, (result) ->
				$scope.list()

		$scope.showEdit = (id) ->
			pathWithId = $leonAngularUtils.setRouteParameter editRoutePath, "id", id
			$leonAngularUtils.showRoute pathWithId 

		# init
		$scope.model = {} if !$scope.model?
		$scope.list()



###
TODO: comment
###
leonAngular.crud.createDefaultEditController = (serverServicePath, listRoutePath) ->
	($scope, $leon, $leonAngularUtils, $routeParams) ->
		$scope.leon = $leonAngularUtils.createScopedLeon($scope, $leon) if !$scope.leon?

		$scope.delete = ->
			$scope.leon.service(serverServicePath, "delete").call $scope.model.current._id, (result) ->
				$scope.showList()

		$scope.save = -> 
			$scope.leon.service(serverServicePath, "save").call $scope.model.current, (result) ->
				$scope.model.current = result

		$scope.showList = ->
			$leonAngularUtils.showRoute listRoutePath

		$scope.get = ->
			if $routeParams.id? and $routeParams.id != ""
				$scope.leon.service(serverServicePath, "get").call $routeParams.id, (result) ->
					$scope.model.current = result
					$scope.model.showDelete = true
			else
				$scope.model.current = {}
				$scope.model.showDelete = false


		# init
		$scope.model = {} if !$scope.model?
		$scope.get()



###
TODO: comment
###
leonAngular.crud.configureWithDefaults = (serverServicePath) ->
	leonAngular.crud.configure { serverServicePath: serverServicePath }



###
TODO: comment
###
leonAngular.crud.configure = ({module, routePrefix, listRoute, editRoute, defaultRoute, templatePrefix, listTemplate, editTemplate, listController, editController, serverServicePath}) ->
	
	###
	define parameter default values
	###
	module ?= leonAngular.crud.module
	routePrefix ?= ""
	listRoute ?= "/list"
	editRoute ?= "/edit/:id"
	defaultRoute = listRoute if defaultRoute is undefined
	templatePrefix ?= "partials/"
	listTemplate ?= "list.html"
	editTemplate ?= "edit.html"


	listRoutePath = leonAngular.utils.assemblePath "/", routePrefix, listRoute
	editRoutePath = leonAngular.utils.assemblePath "/", routePrefix, editRoute
	defaultRoutePath = leonAngular.utils.assemblePath "/", routePrefix, defaultRoute if defaultRoute?


	if !listController? and serverServicePath?
		listController = leonAngular.crud.createDefaultListController serverServicePath, editRoutePath
	else
		throw "neither listController nor serverServicePath (needed by default controller) given!"

	if !editController? and serverServicePath?
		editController = leonAngular.crud.createDefaultEditController serverServicePath, listRoutePath
	else
		throw "neither editController nor serverServicePath (needed by default controller) given!"


	module.config ($routeProvider) ->
		listRouteConfig = { template: leonAngular.utils.assemblePath templatePrefix, listTemplate }

		if listController?
			listRouteConfig.controller = listController

		$routeProvider.when listRoutePath, listRouteConfig

		editRouteConfig = { template: leonAngular.utils.assemblePath templatePrefix, editTemplate }

		if editController?
			editRouteConfig.controller = editController

		$routeProvider.when editRoutePath, editRouteConfig			

		if defaultRoutePath?
			$routeProvider.otherwise { redirectTo: defaultRoutePath }





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
