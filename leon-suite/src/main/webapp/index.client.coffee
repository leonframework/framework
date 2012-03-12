
@IndexController = getLeon().angular.utils.createCrudController ->

    @leon.comet.addHandler "newAddress", =>
        console.log("angular comet")


    @doList = ->
        @leon.service("/indexService", "list").call (result) =>
            @model.list = result

    @doSave = ->
        @leon.service("/indexService", "save").call @model.current, (result) =>
            if result
                @leon.service("/aService", "addressSaved").call @model.current
                @showList()
            else
                alert("Stephan nicht erlaubt!")

    @doEdit = (id) ->
        @leon.service("/indexService", "edit").call id, (data) =>
            @model.current = data


@newAddress = (data) ->
    console.log("comet message")