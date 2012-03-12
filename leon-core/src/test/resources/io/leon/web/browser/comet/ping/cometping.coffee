
getLeon().subscribeTopic "ping", (data) ->
    if data.number
        $("#result").prepend(data.number + "<br/>")
    else if data.done
        $("#isDone").text("true")

@sendPing = ->
    getLeon().service("/pingService", "ping").call 0

