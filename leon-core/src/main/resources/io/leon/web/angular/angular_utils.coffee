
@getLeon().angular = {} if !@getLeon().angular?
@getLeon().angular.utils = {} if !@getLeon().angular.utils?

@getLeon().angular.utils.createCrudController = (controller) ->
	() ->
		# --- required callbacks ---
		@doList = ->
			throw "doList() not implemented"

		@doEdit = ->
			throw "doEdit(id) not implemented"

		@doEditNew = ->
		    @model.current = {}

		# --- state ---

		@route = @$service("$route")

		@location = @$service("$location")

		@leon = @$service("leon")

		@model = {}

		# --- UI view functions ---

		@showRoute = (segment) ->
			@location.update({hashPath: segment})

		@showList = ->
			@showRoute("/list")

		@showEdit = (id) ->
			if id?
				@showRoute("/edit/" + id)
			else
				@showRoute("/edit/")

		# --- default route settings ---

		@routeListUrl = "/list"
		@routeListTemplate = "partials/list.html"

		@routeEditUrl = "/edit/:id"
		@routeEditTemplate = "partials/edit.html"

		# --- user controller ---

		controller.apply(this)

		# --- route settings ---

		@route.parent(this)
		@route.onChange =>
			@params = @route.current.params

		@route.when @routeListUrl,
			template: @routeListTemplate
			controller: ->
				@doList()

		@route.when @routeEditUrl,
			template: @routeEditTemplate
			controller: ->
				if @params.id
					@doEdit(@params.id)
				else
					@doEditNew()

		@route.otherwise redirectTo: @routeListUrl
