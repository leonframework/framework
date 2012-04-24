
# use all the defaults, works out of the box
#@getLeon().angular.crud.configureWithDefaults "/indexService"

@getLeon().angular.crud.configure
	serverServiceUrl: "/indexService"
	routePrefix: "/addresses"

@getLeon().angular.crud.configure
	serverServiceUrl: "/indexService"
	routePrefix: "/test"
	defaultRoute: null
	createFunction: ($scope, callback) ->
		callback({ name: "test", mail: "test@ww.com" })


angular.module("leonCrudApp").controller "MainController", ($scope, $location) ->
    $scope.showList = ->
        $location.path("/addresses/list")

    $scope.showEdit = ->
        $location.path("/addresses/edit/")
