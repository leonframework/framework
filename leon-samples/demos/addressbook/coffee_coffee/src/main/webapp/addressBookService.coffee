
logger = leon.getLogger("addressBookService")

coll = -> leon.mongo.addresses

@addressBookService =
	save: (address) ->
		coll().save(address)

	list: ->
		coll().find().toArray()

	get: (id) ->
		logger.info("Loading address with ID " + id)
		coll().findOne({"firstname": "Roman"})

