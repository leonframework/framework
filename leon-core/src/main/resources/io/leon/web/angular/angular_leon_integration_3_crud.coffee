# ----------
# In this file everything of leon's basic crud support is defined. They depend on the utils defined in a previous step.
# Basic crud support contains a module leon.curd, controllers for list view and edit view, utils to register the needed
# crud service as well as a default implementation.
# ----------



@getLeon().angular.crud = {} if!@getLeon().angular.crud?
@getLeon().angular.crud.module = angular.module 'leon.crud', ['ng']



# TODO: comment
@getLeon().angular.crud.module.controller "CrudListController", ($scope, $crudService, $leonAngularUtils) ->
	
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



# TODO: comment
@getLeon().angular.crud.module.controller "CrudEditController", ($scope, $crudService, $leonAngularUtils, $routeParams) ->
	
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


# TODO: comment
@getLeon().angular.crud.module.config ($routeProvider) -> 
	$routeProvider.when '/list', { template: 'partials/list.html', controller: "CrudListController" }
	$routeProvider.when '/edit/:id', { template: 'partials/edit.html', controller: "CrudEditController" }
	$routeProvider.otherwise { redirectTo: '/list' }


###
TODO: comment
###
@getLeon().angular.crud.registerService = (service) =>
	@getLeon().angular.crud.module.service "$crudService", service



###
TODO: comment
###
@getLeon().angular.crud.createaDefaultService = (serverServicePath) =>
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
@getLeon().angular.crud.createAndRegisterDefaultService = (serverServicePath) =>
	service = @getLeon().angular.crud.createaDefaultService serverServicePath
	@getLeon().angular.crud.registerService service



# ----------
# expose leon angular crud support as angular service to enable di
# ----------

# constructor function to use as service provider with angular
crudService = () ->
	# place to add angular specific things 
	# do it like this: @aNewFunction = ...


# make everything available defined in loen's angular crud support
crudService.prototype = getLeon().angular.crud

@getLeon().angular.leonModule.service "$leonAngularCrud", crudService
