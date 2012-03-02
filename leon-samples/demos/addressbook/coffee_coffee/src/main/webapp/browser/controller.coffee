

@AddressBookController = getLeon().angular.utils.createCrudController ->
	@doEdit = (id) ->
		@leon.service("/addressBookService", "get").call id, (a) =>
			@model.current = a

	@doList = ->
		@leon.service("/addressBookService", "list").call (list) =>
			@model.list = list

	@doDelete = (id) ->
		@leon.service("addressBookService", "delete").call id, =>
			@doList()

	@doSave = ->
		@leon.service("/addressBookService", "save").call(@model.current)
		@showList()
