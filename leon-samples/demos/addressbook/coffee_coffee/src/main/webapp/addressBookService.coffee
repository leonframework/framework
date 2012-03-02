
logger = leon.getLogger("addressBookService")

# Primary keys
ids = new Packages.java.util.concurrent.atomic.AtomicLong(0)
newId = -> ids.getAndIncrement().toString()

# Mock DB
db = {}

# DB functions
dbSave = (o) ->
	o._id = newId() if !o._id?
	db[o._id] = o

dbDelete = (id) ->
	delete db[id]

# Init
dbSave({ firstname: "A", lastname: "a", phone: "1" })
dbSave({ firstname: "B", lastname: "b", phone: "2" })
dbSave({ firstname: "C", lastname: "c", phone: "3" })
dbSave({ firstname: "D", lastname: "d", phone: "4" })
dbSave({ firstname: "E", lastname: "e", phone: "5" })
dbSave({ firstname: "F", lastname: "f", phone: "6" })
dbSave({ firstname: "G", lastname: "g", phone: "7" })
dbSave({ firstname: "H", lastname: "h", phone: "8" })
dbSave({ firstname: "I", lastname: "i", phone: "9" })
dbSave({ firstname: "J", lastname: "j", phone: "10" })
dbSave({ firstname: "K", lastname: "k", phone: "11" })
dbSave({ firstname: "L", lastname: "l", phone: "12" })
dbSave({ firstname: "M", lastname: "m", phone: "13" })
dbSave({ firstname: "N", lastname: "n", phone: "14" })
dbSave({ firstname: "O", lastname: "o", phone: "15" })
dbSave({ firstname: "P", lastname: "p", phone: "16" })

# ---------------------------------------------------------

# Servie
@addressBookService =
	save: (address) ->
		dbSave(address)

	delete: (id) ->
		dbDelete(id)

	list: ->
		for k, v of db
			v._id = k
			v

	get: (id) ->
		for k, v of db
			if k is id
				v._id = k
				return v

