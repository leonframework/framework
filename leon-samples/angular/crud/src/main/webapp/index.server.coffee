
db = -> leon.mongo.addresses

@indexService =
	list: ->
		db().find().sort({name:1, mail:-1}).toArray()

	get: (id) ->
		db().findByOId(id)

	save: (data) ->
		db().save(data)

	delete: (id) ->
		data = db().findByOId(id)

		if data?
			db().remove(data)
			true
		else
			false