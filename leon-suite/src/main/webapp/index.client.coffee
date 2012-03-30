
@IndexController = getLeon().angular.utils.createController ($scope) ->
  @getMessage = ->
    @leon.service("/demoService", "getA").call (data) =>
        @model.a = data

  @getMessage()
