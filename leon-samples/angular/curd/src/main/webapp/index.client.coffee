
# use all the defaults, works out of the box
#@getLeon().angular.crud.configureWithDefaults "/indexService"


@getLeon().angular.crud.configure
	serverServicePath: "/indexService"
	routePrefix: "/addresses"


@getLeon().angular.leonAppCrudModule.controller "MainController", ($scope, $leonAngularUtils) ->
    $scope.showList = ->
        $leonAngularUtils.showRoute "/addresses/list"

    $scope.showEdit = ->
        $leonAngularUtils.showRoute "/addresses/edit/"
