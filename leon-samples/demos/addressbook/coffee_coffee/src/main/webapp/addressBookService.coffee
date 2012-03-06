
logger = leon.getLogger("addressBookService")

db = -> leon.mongo.addresses

# Servie
@addressBookService =
	save: (address) ->
		db().save(address)

	delete: (id) ->
		db().removeByOId(id)

	list: ->		
		db().find().toArray()

	get: (id) ->
		db().findByOId(id)
