
@DummyAppCtrl = (leon) ->
	leon.subscribeTopic "reversed", (d) =>
		$("#reversedStrings").prepend(d.original + " > " + d.reversed + "<br/>")
		@cometLastReversed = d.reversed

	@text = "Hello World!"
	@reversed = ""

	@reverse = ->
		leon.service("/reverserService", "reverse").call @text, (@reversed) =>


