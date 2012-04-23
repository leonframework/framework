
this.IndexController = getLeon().angular.utils.createController(
	function() {
		this.getMessage = function() {
			var scope = this;
			this.leon.service("/demoService", "getExampleMessage").call(
				function(data) {
					scope.model.message = data;
				}
			)
		};
     
		this.getMessage();
	}
)