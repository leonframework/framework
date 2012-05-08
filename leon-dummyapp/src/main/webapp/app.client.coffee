
@DummyAppCtrl = ($scope, leon) ->
	leon.subscribeTopic "/reversed", (d) =>
		$("#reversedStrings").prepend(d.original + " > " + d.reversed + "<br/>")
		$scope.cometLastReversed = d.reversed

	$scope.text = "Hello World!"
	$scope.reversed = ""

	$scope.reverse = ->
		leon.service("/reverserService", "reverse").call $scope.text, (reversed) =>
      $scope.reversed = reversed
