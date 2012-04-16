
@IndexController = getLeon().angular.utils.createController ->
  @getMessage = ->
    @leon.service("/demoService", "getExampleMessage").call (data) =>
        @model.a = data

  @getMessage()
