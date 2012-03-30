
# initialize -> create objects if undefined
@getLeon().angular = {} if !@getLeon().angular?
@getLeon().angular.utils = {} if !@getLeon().angular.utils?
@getLeon().angular.injector = angular.injector(['ng', 'leon']) if !getLeon().angular.injector?

###
Use this function instead of getLeon() to get a modified leon object
if you want the given scope to be updated after

- a service call, more accurate: after the given callback was applied
  leon.service(serviceUrl, methodName).call(callback)
- a comet message was received, more accurate: after the given handler was applied
  leon.subscribeTopic(topicId, handler)
###
@getLeon().angular.utils.createScopedLeon = (scope) ->
  leon = getLeon().angular.injector.get("$leon")
  scopedLeon = {}
  scopedLeon.__proto__ = leon
  
  # override service function of prototype to enable refreshing the scope
  scopedLeon.service = (url, methodName) ->
    call: (args...) ->
      refreshHook = () ->
        scope.$digest()
      leon.service(url, methodName, refreshHook).call.apply(this, args)

  # override subscribeTopic function of prototype to enable refreshing the scope
  scopedLeon.subscribeTopic = (topicId, handler) ->
    scopedHandler = (data) ->
      handler(data)
      scope.$digest()
    
    leon.comet.subscribeTopic topicId, scopedHandler

  scopedLeon

###
TODO: comment!
###
@getLeon().angular.utils.createController = (controller) ->
	(scope) ->
	 
	  # --- services ---
		
		scope.$location = getLeon().angular.injector.get("$location")
		scope.$leon = getLeon().angular.utils.createScopedLeon(scope)
		
		# --- state ---
		
		scope.model = new Object()

		# --- user controller ---

		controller.apply(scope)


###
TODO: comment!
###
@getLeon().angular.utils.createRouteController = (controller) ->
	(scope) ->
		# --- services ---
		scope.$route = getLeon().angular.injector.get("$route")
    scope.$routeProvider = getLeon().angular.injector.get("$routeProvider")
		scope.$location = getLeon().angular.injector.get("$location")
		scope.$leon = getLeon().angular.utils.createScopedLeon(scope)

    # --- state ---
		
		scope.model = new Object()

		# --- UI view functions ---

		scope.showRoute = (path) ->
		    scope.location.hash(path)

		# --- route functions ---

		scope.addRoute = (url, template, fn) ->
        scope.$routeProvider.when url,
            template: template
            controller: fn

    scope.setDefaultRoute = (url) ->
        scope.$routeProvider.otherwise redirectTo: url

		# --- routes ---

		scope.route.$afterRouteChange (current, previous) =>
        scope.params = current.params

		# --- user controller ---

		controller.apply(scope)


###
TODO: comment!
###
@getLeon().angular.utils.createCrudController = (controller) ->
	(scope) ->
		
		# --- required callbacks ---
		
		scope.doList = ->
			throw "doList() not implemented"

		scope.doEdit = ->
			throw "doEdit(id) not implemented"

		scope.doEditNew = ->
		    @model.current = {}

		# --- services ---
    
		scope.$route = getLeon().angular.injector.get("$route")
    scope.$routeProvider = getLeon().angular.injector.get("$routeProvider")
		scope.$location = getLeon().angular.injector.get("$location")
		scope.$leon = getLeon().angular.utils.createScopedLeon(scope)

    # --- state ---

		scope.model = {}

		# --- UI view functions ---

		scope.showRoute = (segment) ->
			scope.location.hash(segment)

		scope.showList = ->
			scope.showRoute("/list")

		scope.showEdit = (id) ->
			if id?
				scope.showRoute("/edit/" + id)
			else
				scope.showRoute("/edit/")

		# --- default route settings ---

		scope.routeListUrl = "/list"
		scope.routeListTemplate = "partials/list.html"

		scope.routeEditUrl = "/edit/:id"
		scope.routeEditTemplate = "partials/edit.html"

		# --- user controller ---

		controller.apply(scope)

		# --- route settings ---

		scope.$route.$afterRouteChange (current, previous) =>
      scope.params = current.params

		scope.$routeProvider.when scope.routeListUrl,
			template: scope.routeListTemplate
			controller: ->
				scope.doList()

		scope.$routeProvider.when scope.routeEditUrl,
			template: scope.routeEditTemplate
			controller: ->
				if scope.params.id
					scope.doEdit(scope.params.id)
				else
					scope.doEditNew()

		scope.$routeProvider.otherwise redirectTo: scope.routeListUrl
