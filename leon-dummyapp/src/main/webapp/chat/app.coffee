@ChatCtrl = ($scope, leon) ->
  $("#message").keypress (e) ->
    if e.which == 13
      $("#send").click()
      e.preventDefault()
      false

  $scope.addMessage = (msg) ->
    $scope.messages.unshift(msg)

  $scope.messages = []
  $scope.user = "John"

  leon.subscribeTopic "chat", (d) ->
    $scope.addMessage(d)

  $scope.send = ->
    leon.service("/chat", "newMessage").call($scope.user, $scope.message)
    $scope.message = ""
