/**
 * Controller for the index.html file.
 *
 * @param $scope Angular scope object used to store the state and scope-related functions.
 * @param leon Leon's browser API extends with Angular-specific features (e.g. automatic
 *             scope refresh after Ajax call handler execution).
 */
this.IndexController = function($scope, leon) {
    // Function to fetch a new random message from the server
    $scope.getMessage = function() {
        // Call the server function DemoService.getExampleMessage ...
		leon.service("/demoService", "getExampleMessage").call(function(data) {
            // ... and store the result in the scope object.
			$scope.message = data;
		})
	};
    // Get a message from the server.
	$scope.getMessage();
};
