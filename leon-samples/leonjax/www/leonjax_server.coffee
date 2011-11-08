global.leonJaxService =
  postMessage: (user, room, message) ->
    leon.publishMessage("leonjax.room.messages", { "room": room }, message)

  getRoomList: ->
    ["München",
    "Calgary",
    "Sidney",
    "Partenkirchen",
    "Ballsall A",
    "Ballsall B",
    "Barcelona"]
