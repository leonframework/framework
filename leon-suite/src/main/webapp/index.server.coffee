
db = -> leon.mongo.addresses

@indexService =
    list: ->
        db().find().sort({name:1, mail:-1}).toArray()

    edit: (id) ->
        db().findByOId(id)

    save: (data) ->
        if data.name is "Stephan"
            false
        else
            db().save(data)
            ts = leon.inject(Packages.io.leon.web.TopicsService)
            ts.send("newAddress", data)
            true