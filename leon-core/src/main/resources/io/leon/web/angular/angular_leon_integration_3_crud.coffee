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
leonAngular.crud.createDefaultListController = (serverServicePath, editRoutePath, listFunction, deleteFunction) ->
	($scope, $leon, $leonAngularUtils, $injector) ->
		$scope.leon = $leonAngularUtils.createScopedLeon($scope, $leon) if !$scope.leon?

		$scope.doList = listFunction
		$scope.doDelete = deleteFunction

		$scope.list = ->
			callback = (result) ->
				$scope.model.list = result

			$injector.invoke $scope.doList, this, { $scope: $scope, callback: callback }

		$scope.delete = (id) ->
			callback = (result) ->
				$scope.list()

			$injector.invoke $scope.doDelete, this, { $scope: $scope, id: id, callback: callback } 

		$scope.showEdit = (id) ->
			pathWithId = $leonAngularUtils.setRouteParameter editRoutePath, "id", id
			$leonAngularUtils.showRoute pathWithId 

		# init
		$scope.model = {} if !$scope.model?
		$scope.list()



###
TODO: comment
###
leonAngular.crud.createDefaultEditController = (serverServicePath, listRoutePath, saveFunction, deleteFunction, getFunction, createFunction) ->
	($scope, $leon, $leonAngularUtils, $routeParams, $injector) ->
		$scope.leon = $leonAngularUtils.createScopedLeon($scope, $leon) if !$scope.leon?

		$scope.doDelete = deleteFunction
		$scope.doGet = getFunction
		$scope.doCreate = createFunction
		$scope.doSave = saveFunction

		$scope.delete = (id) ->
			callback = (result) ->
				$scope.showList()

			$injector.invoke $scope.doDelete, this, { $scope: $scope, id: $scope.model.current._id, callback: callback }

		$scope.save = ->
			callback = (result) ->
				$scope.model.current = result

			$injector.invoke $scope.doSave, this, {$scope: $scope, data: $scope.model.current, callback: callback }

		$scope.get = ->
			if $routeParams.id? and $routeParams.id != ""
				callback = (result) ->
					$scope.model.current = result
					$scope.model.showDelete = true

				$injector.invoke $scope.doGet, this, { $scope: $scope, id: $routeParams.id, callback: callback }
			else
				callback = (data) ->
					$scope.model.current = data
					$scope.model.showDelete = false
				$injector.invoke $scope.doCreate, this, { $scope: $scope, callback: callback }

		$scope.showList = ->
			$leonAngularUtils.showRoute listRoutePath


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
leonAngular.crud.configure = ({module, routePrefix, listRoute, editRoute, defaultRoute, templatePrefix, listTemplate, editTemplate, listController, editController, serverServicePath, listFunction, saveFunction, deleteFunction, getFunction, createFunction }) ->
	
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

	listFunction ?= ($scope, callback)->
		$scope.leon.service(serverServicePath, "list").call callback

	deleteFunction ?= ($scope, id, callback) ->
		$scope.leon.service(serverServicePath, "delete").call id, callback

	saveFunction ?= ($scope, data, callback) -> 
		$scope.leon.service(serverServicePath, "save").call data, callback

	getFunction ?= ($scope, id, callback) ->
		$scope.leon.service(serverServicePath, "get").call id, callback

	createFunction ?= ($scope, callback) ->
		callback({})

	listRoutePath = leonAngular.utils.assemblePath "/", routePrefix, listRoute
	editRoutePath = leonAngular.utils.assemblePath "/", routePrefix, editRoute
	defaultRoutePath = leonAngular.utils.assemblePath "/", routePrefix, defaultRoute if defaultRoute?
	prefixRoutePath = leonAngular.utils.assemblePath "/", routePrefix if routePrefix?


	if !listController? and serverServicePath?
		listController = leonAngular.crud.createDefaultListController serverServicePath, editRoutePath, listFunction, deleteFunction
	else if !listController?
		throw "neither listController nor serverServicePath (needed by default controller) given!"

	if !editController? and serverServicePath?
		editController = leonAngular.crud.createDefaultEditController serverServicePath, listRoutePath, saveFunction, deleteFunction, getFunction, createFunction
	else if !editController?
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

		if prefixRoutePath?
			$routeProvider.when prefixRoutePath, { redirectTo: listRoutePath } 	

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
