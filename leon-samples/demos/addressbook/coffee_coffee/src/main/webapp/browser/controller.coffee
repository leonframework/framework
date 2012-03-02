
@AddressBookController = ($route, leon) ->
	# controllers
	@ListController = ->
		@loadAll()

	@EditController = (leon) ->
		if @params.id
			leon.service("/addressBookService", "get").call @params.id, (a) =>
				@model.address = a
		else
			@model.address = {}


	# state
	@model = {}

	# route settings
	$route.parent(this)
	$route.onChange =>
		@params = $route.current.params

	$route.when "/list",
		template: "partials/list.html"
		controller: @ListController

	$route.when "/edit/:id",
		template: "partials/edit.html"
		controller: @EditController

	$route.otherwise redirectTo: '/list'

	# functions
	@loadAll = ->
		leon.service("/addressBookService", "list").call (list) =>
			@model.list = list

	@delete = (id) ->
		leon.service("addressBookService", "delete").call id, =>
			@loadAll()

	@save = ->
		leon.service("/addressBookService", "save").call(@model.address)
		leon.go("#/list")
