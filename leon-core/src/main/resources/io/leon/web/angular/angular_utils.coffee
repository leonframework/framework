
@getLeon().angular = {} if !@getLeon().angular?
@getLeon().angular.utils = {} if !@getLeon().angular.utils?

@getLeon().angular.utils.createCrudController = (controller) ->
	() ->
		# --- required callbacks ---
		@doList = ->
			throw "doList() not implemented"

		@doEdit = ->
			throw "doEdit(id) not implemented"

		# --- state ---

		@route = @$service("$route")

		@leon = @$service("leon")

		@model = {}

		# --- UI view functions ---

		@showList = ->
			@leon.go("#/list")

		@showEdit = (id) ->
			if id?
				@leon.go("#/edit/" + id)
			else
				@leon.go("#/edit/")

		# --- default route settings ---

		@routeListTemplate = "partials/list.html"
		@routeEditTemplate = "partials/edit.html"

		# --- user controller ---

		controller.apply(this)

		# --- route settings ---

		@route.parent(this)
		@route.onChange =>
			@params = @route.current.params

		@route.when "/list",
			template: @routeListTemplate
			controller: ->
				@doList()

		@route.when "/edit/:id",
			template: @routeEditTemplate
			controller: ->
				if @params.id
					@doEdit(@params.id)
				else
					@model.current = {}

		@route.otherwise redirectTo: '/list'
