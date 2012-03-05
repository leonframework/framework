
#----------------------------------------------------------
# UI elements / CSS
#----------------------------------------------------------

$('<link rel="stylesheet" href="/io/leon/web/cockpit/leon-cockpit.css"/>').appendTo("head")

menu = $("""
<div id="leonCockpitMenu">
	<span class="logo"><img src="/io/leon/web/cockpit/leon_small.png" /></span>
	<span class="info">Leon Cockpit</span>
	<span class="buttons">
		<span class="button_active"><a href="#">Auto Refresh</a></span>
		|
		<span class="button"><a href="#">Hightlight Changes</a></span>
		|
		<span class="button"><a href="#">Actions</a></span>
		|
		<span class="button"><a href="#">Server REPL</a></span>
	</span>
	<div class="log"></div>
</div>
""")

logLabel = -> $("#leonCockpitMenu>div.log")

#----------------------------------------------------------
# State
#----------------------------------------------------------

logMessageTimer = null

#----------------------------------------------------------
# Internal functions
#----------------------------------------------------------

showMenu = ->
	$("body").first().addClass("leonCockpitBodyLowered")
	$("body").prepend(menu)
	menu.show()

hideMenu = ->
	$("body").first().removeClass("leonCockpitBodyLowered")
	menu.hide()

getCurrentTime = ->
	date = new Date()
	hours = date.getHours()
	hours = "0" + hours if hours < 10
	minutes = date.getMinutes()
	minutes = "0" + minutes if minutes < 10
	seconds = date.getSeconds()
	seconds = "0" + seconds if seconds < 10
	hours + ":" + minutes + ":" + seconds

#----------------------------------------------------------
# Public API
#----------------------------------------------------------

getLeon().cockpit =
	enable: ->
		showMenu()
		getLeon().comet.connect()
		getLeon().comet.addHandler "leon.developmentMode.resourceWatcher.resourceChanged", (data) ->
			getLeon().log("Reloaded [" + data.name + "]")
		console.log("Leon Cockpit enabled.")

	disable: ->
		console.log("Leon Cockpit disabled.")
		hideMenu()

	displayLogMessage: (message) ->
		date = new Date()
		clearInterval(logMessageTimer)
		hideLogMessage = -> logLabel().text("")
		logMessageTimer = setTimeout(hideLogMessage, 10000)
		logLabel().text(getCurrentTime() + " " + message)

#----------------------------------------------------------
# Init
#----------------------------------------------------------

$ => getLeon().cockpit.enable()

