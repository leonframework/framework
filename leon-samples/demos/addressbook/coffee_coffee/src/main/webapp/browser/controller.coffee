
@AddressBookController = ($route) ->
	$route.parent this
	$route.onChange => @params = $route.current.params

	$route.when "/list",
		template: "partials/list.html"
		controller: AddressBookListController

	$route.when "/edit/:id",
		template: "partials/edit.html"
		controller: AddressBookEditController

	$route.otherwise redirectTo: '/list'


@AddressBookListController = ->
	leon.service("/addressBookService", "list").call (list) =>
		console.log(list)


@AddressBookEditController = ->
	if @params.id
		@address = {}
	else
		@address = {}

	@save = ->
		console.log(@address)
		leon.service("/addressBookService", "save").call(@address)
