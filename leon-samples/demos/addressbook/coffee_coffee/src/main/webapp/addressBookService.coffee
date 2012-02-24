
@addressBookService =
	save: (address) ->
		leon.mongo.addresses.save(address)

	list: ->
		#data = []
		#leon.mongo.addresses.find().foreach (a) ->
		#	data.push(a)
		#data
		gson = leon.getGson()
		println = (o) -> Packages.java.lang.System.out.println(o)

		println("-----------")
		result = (p for p in leon.mongo.addresses.find().toArray())

		println("result[0] = ")
		println(result[0])
		println("")

		println("gsontoJson(result[0]) = ")
		println(gson.toJson(result[0]))
		println("")

		result




