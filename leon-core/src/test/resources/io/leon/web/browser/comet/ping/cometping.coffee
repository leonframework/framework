
@sendPing = ->
    leon.service("/pingService").call("ping", 0)

@pingReceived = (data) ->
    $("#result").prepend(data + "<br/>")
