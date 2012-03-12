
@IndexController = getLeon().angular.utils.createController ->
    @leon.service("/demoService", "getA").call (data) ->
        @model.a = data
