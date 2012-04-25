
@ChatCtrl = ($scope, $leon) ->
	$("#message").keypress (e) ->
		if e.which == 13
			$("#send").click()
			e.preventDefault()
			false

	$scope.messages = []
	$scope.user = "John"

	$leon.subscribeTopic "chat", (d) ->
		$scope.messages.unshift(d)

	$scope.send = ->
		$leon.service("/chat", "newMessage").call($scope.user, $scope.message)
		$scope.message = ""
