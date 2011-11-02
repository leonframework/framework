global.leonJaxService =
  postMessage: (user, room, message) ->
    leon.publishMessage("leonjax.room.messages", { "room": room }, message)
