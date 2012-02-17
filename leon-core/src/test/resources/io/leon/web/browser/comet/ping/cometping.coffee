
@sendPing = ->
    leon.service("/pingService").call("ping", 0)

@pingReceived = (data) ->
    if data.number
        $("#result").prepend(data.number + "<br/>")
    else if data.done
        $("#isDone").prepend("true")

