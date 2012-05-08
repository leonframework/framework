
@ShiroCtrl = ($scope, leon) ->
  $scope.username = "admin"
  $scope.password = "secret"
  $scope.messages = []

  $scope.auth = ->
    leon.service("/shiro", "auth").call $scope.username, $scope.password, (response) ->
      $scope.messages.unshift(response)

  $scope.doPrivate1 = ->
    leon.service("/shiro", "doPrivate1").call (response) ->
      $scope.messages.unshift(response)

  $scope.doPrivate2 = ->
    leon.service("/shiro", "doPrivate2").call (response) ->
      $scope.messages.unshift(response)

  $scope.doPublic = ->
    leon.service("/shiro", "doPublic").call (response) ->
      $scope.messages.unshift(response)

