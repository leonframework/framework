
@IndexController = getLeon().angular.utils.createController ($scope) ->
  $scope.refresh = ->
    $scope.leon.service("/demoService", "getA").call (data) =>
        $scope.model.a = data
        $scope.$digest()

  $scope.refresh()
