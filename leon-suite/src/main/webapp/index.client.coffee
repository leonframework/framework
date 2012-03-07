
@IndexController = getLeon().angular.utils.createController ->
	@setDefaultRoute("/serviceA")

	@addRoute "/serviceA", "/partials/serviceA.html", ->
		@leon.service("/indexService", "serviceA").call (data) =>
			@model.data = data

	@addRoute "/serviceB", "/partials/serviceB.html", ->
		@leon.service("/indexService", "serviceB").call (data) =>
			@model.data = data

	@addRoute "/serviceAB", "/partials/serviceAB.html", ->
		@leon.service("/indexService", "serviceAB").call (data) =>
			@model.data = data
