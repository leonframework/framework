@leonJaxService =
  postMessage: (user, room, message) ->
    leon.publishMessage("leonjax.room.messages", { "room": room }, {user: user, message: message } )

  getRoomList: ->
    ["München",
    "Calgary",
    "Sidney",
    "Partenkirchen",
    "Ballsall A",
    "Ballsall B",
    "Barcelona"]
