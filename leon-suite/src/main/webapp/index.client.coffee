
@IndexController = getLeon().angular.utils.createController ->
  @getMessage = ->
    @leon.service("/demoService", "getA").call (data) =>
        @model.a = data

  @getMessage()
