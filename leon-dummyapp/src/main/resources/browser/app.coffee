
@DummyAppCtrl = ->
    @text = "Hello World!"
    @reversed = ""

    @reverse = ->
        leon.service("/reverserService", "reverse").call @text, (@reversed) =>

@reversedTopic = (d) ->
	$("#reversedStrings").prepend(d.original + "->" + d.reversed + "<br/>")

