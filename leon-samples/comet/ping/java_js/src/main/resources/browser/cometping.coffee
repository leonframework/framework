@sendPing = ->
  server.pingService("ping")(0)

@pingReceived = (data) ->
  console.log data
