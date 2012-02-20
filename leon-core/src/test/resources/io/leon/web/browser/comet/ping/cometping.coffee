
@sendPing = ->
    leon.service("/pingService", "ping").call 0

@pingReceived = (data) ->
    if data.number
        $("#result").prepend(data.number + "<br/>")
    else if data.done
        $("#isDone").text("true")

