logger = leon.getLogger("jeonjax_server")

@leonJaxService =
  postMessage: (user, room, message) ->
    logger.info("New message from " + user)
    leon.publishMessage("leonjax.room.messages", { "room": room }, {user: user, message: message } )

  getRoomList: ->
    ["München",
    "Calgary",
    "Sidney",
    "Partenkirchen",
    "Ballsall A",
    "Ballsall B",
    "Barcelona"]
