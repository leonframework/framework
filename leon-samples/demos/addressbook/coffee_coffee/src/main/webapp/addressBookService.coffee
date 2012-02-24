
@addressBookService =
	save: (address) ->
		leon.mongo.addresses.save(address)

	list: ->
		#data = []
		#leon.mongo.addresses.find().foreach (a) ->
		#	data.push(a)
		#data
		println = (o) -> Packages.java.lang.System.out.println(o)

		(p for p in leon.mongo.addresses.find().toArray())




