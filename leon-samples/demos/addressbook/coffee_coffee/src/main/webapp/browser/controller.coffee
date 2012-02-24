
@AddressBookController = ($route) ->
	$route.parent(this)
	$route.onChange =>
		@params = $route.current.params

	$route.when "/list",
		template: "partials/list.html"
		controller: AddressBookListController

	$route.when "/edit/:id",
		template: "partials/edit.html"
		controller: AddressBookEditController

	$route.otherwise redirectTo: '/list'

	@delete = (id) ->
		console.log("delete " + id)
		console.log($route)


@AddressBookListController = ->
	leon.service("/addressBookService", "list").call (list) =>
		@view = list


@AddressBookEditController = ->
	if @params.id
		leon.service("/addressBookService", "get").call @params.id, (a) =>
			console.log(a)
			@address = a
	else
		@address = {}

	@save = ->
		console.log(@address)
		leon.service("/addressBookService", "save").call(@address)
