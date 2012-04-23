
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


@getLeon().angular.leonAppCrudModule.controller "MainController", ($scope, $leonAngularUtils) ->
    $scope.showList = ->
        $leonAngularUtils.showRoute "/addresses/list"

    $scope.showEdit = ->
        $leonAngularUtils.showRoute "/addresses/edit/"
