
this.IndexController = function($scope, leon) {
	$scope.getMessage = function() {
		leon.service("/demoService", "getExampleMessage").call(function(data) {
			$scope.message = data;
		})
	};
	
	$scope.getMessage();
}
