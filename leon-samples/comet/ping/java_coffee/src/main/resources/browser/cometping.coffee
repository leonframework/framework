@sendPing = ->
    server.pingService("ping")(0)

@pingReceived = (data) ->
    $("#result").prepend(data + "<br/>")
