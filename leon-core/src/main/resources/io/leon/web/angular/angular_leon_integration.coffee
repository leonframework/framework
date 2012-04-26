# ----------
# This is the first part of the angular leon integration. To keep it clear, the integration is split into several files,
# each containing code for a specific topic.
#
# Define things depending on angular only in this file! All other things like leon angular utils are not defined yet.
# The other integration steps can depend on the things defined here.
#
# Basically we define an angular module for leon. All the other services leon provides, can be registered at this
# module and other modules can depend on it to use DI.
# ----------


# create a angular module to support tight integration with angular
# customer modules can depend on this to get access to leon services
leonCoreModule = angular.module('leon.core', ['ng'])


#----------
# expose leon browser support as angular service to enable usage via DI
#----------

#----------
# Register an unmodified version of leon as service. The unmodified service is needed by the modified version of leon
# (with angular specifics) below. We can't use the prototype / __proto__ in the modified version which is set to
# unmodified leon, because 'this' is bound to something different when a service function is called and therefor we
# can't reach the correct prototype.
#----------
leonUnmodified = ->
leonUnmodified.prototype = getLeon()
leonCoreModule.service("leonUnmodified", leonUnmodified)

#----------
# Register a modified version of leon as angular service. This modified version is used for dependency injection by
# angular.
# Leon's communication between the browser and the server is asynchronous. If you tell leon to call a service
# on a server or you register for a topic, you'll have to give a callback function to leon, which is called when the
# server send data. In this callback you can proceed the server response.
# Angular is able to detect changes to the scope and even to services like $location in specific life-cycle steps
# only ($digest). The callback function you gave is called outside this life-cycle step. To enable angular to detect the
# changes made in the given callback function, we must call this function within the $apply method of a scope. The
# $scope.$apply method calls the given function, which calls the callback. So the callback is called within $apply.
# $apply calls $rootScope.$digest (see angular API documentation of $apply). So there is no difference between using
# the $apply method of a specific scope or the $apply method of $rootScope. $rootScope can be injected, so we don't need
# to carry the specific scope with us.
#----------
leonModified = (leonUnmodified, $rootScope) ->
  @service = (url, methodName) ->
    call: (args...) ->
      params = []
      callback = args[args.length - 1]

      if angular.isFunction(callback)
        params = args.slice(0, args.length - 1)

        scopedCallback = (result) ->
          $rootScope.$apply ->
            callback(result)

        params.push(scopedCallback)
      else
        params = args.slice(0, args.length)

      leonUnmodified.service(url, methodName).call.apply(this, params)

  @subscribeTopic = (topicId, handler) ->
    scopedHandler = (data) ->
      $rootScope.$apply ->
        handler(data)

    leonUnmodified.subscribeTopic(topicId, scopedHandler)

leonModified.prototype = getLeon()

leonModified.$inject = ["leonUnmodified", "$rootScope"]
leonCoreModule.service("leon", leonModified)


# leonAngularUtils is registered as constant (see comment below), so it has to be an object not a constructor function!
# All functions has to be pure! No DI, no state, only little helpers!
leonAngularUtils =
  ###
  Helper function that can be used to add leading and clothing slashes to a path, for example.

  Adds the prefix to the path, if the path doesn't start with the prefix.
  Adds the suffix to the path, if the path doesn't end with the suffix.

  Special case: if prefix equals suffix and path is empty, only prefix will be returned (no double slashes).
  ###
  assemblePath: (elements...) ->
    assembledPath = elements.join "/"

    # triple slashes can occur if one element ends with a slash and the next element starts with a slash!
    assembledPath = assembledPath.replace "///", "/"
    assembledPath = assembledPath.replace "//", "/"

    assembledPath


  ###
  Sets a route parameter like :id to the given value, to given default value (if value is null or empty) or removes the
  parameter and the rest of the route (if default value is null or empty too).
  If the parameter is removed and was the last parameter of the route, the route will close with a slash. That's the way
  angular needs it to match the route.
  If the removed parameter was not the last parameter of the route, the subsequent part of the route will be removed too.
  E.g: setRouteParameter "book/:bookId/chapter/:chapterId", "bookId", "", "" returns "book/"
  ###
  setRouteParameter: (routeString, paramName, paramValue, defaultParamValue) ->
    indexOfParameter = routeString.indexOf ":" + paramName

    if indexOfParameter == -1
      throw "route has no parameter " + paramName + "!"

    if paramValue? and paramValue != ""
      routeWithSetParam = routeString.replace ":" + paramName, paramValue
    else if defaultParamValue? and defaultParamValue != ""
      routeWithSetParam = routeString.replace ":" + paramName, defaultParamValue
    else
      routeWithSetParam = routeString.substring(0, indexOfParameter)

    routeWithSetParam


# We have to register it as constant to enable DI to config functions. All helper functions are pure so it's not as bad
# as it looks.
leonCoreModule.constant "leonAngularUtils", leonAngularUtils


###
A simple module including angular and leon support.
It's ready to use to implement small applications which don't need an own module.
###
angular.module('leonApp', ['ng', 'leon.core'])
