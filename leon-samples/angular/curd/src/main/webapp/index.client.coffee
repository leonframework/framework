
# use all the defaults, works out of the box
#@getLeon().angular.crud.useDefaultConfigWithDefaultService "/indexService"

crudConfig = getLeon().angular.crud.createDefaultConfig "addresses"
@getLeon().angular.crud.setConfig crudConfig
@getLeon().angular.crud.createAndRegisterDefaultService "/indexService"


@getLeon().angular.leonAppCrudModule.controller "MainController", ($scope, $leonAngularUtils, crudConfig) ->
    $scope.showList = ->
        $leonAngularUtils.showRoute crudConfig.listRoute.path

    $scope.showEdit = ->
        route = $leonAngularUtils.setRouteParameter crudConfig.editRoute.path + "/test/bla", "id", ""
        $leonAngularUtils.showRoute route



@getLeon().angular.leonAppCrudModule.controller "AdditionalEditController", ($scope) ->
    $scope.model = {} if !$scope.model?
    $scope.model.edittest = "Edit controller test"
