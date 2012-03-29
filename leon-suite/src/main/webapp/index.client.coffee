
@IndexController = getLeon().angular.utils.createController ($scope) ->
    $scope.leon.service("/demoService", "getA").call (data) =>
        $scope.model.a = data
        $scope.$digest()
