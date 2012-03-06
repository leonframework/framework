
logger = leon.getLogger("addressBookService")

db = -> leon.mongo.addresses

# Servie
@addressBookService =
	save: (address) ->
		db().save(address)

	delete: (id) ->
		db().findAndRemove({"_id": id})

	list: ->		
		db().find().toArray()

	get: (id) ->
		db().findOne({"_id": id})
