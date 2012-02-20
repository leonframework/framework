
#----------------------------------------------------------
# UI elements / CSS
#----------------------------------------------------------

$('<link rel="stylesheet" href="/io/leon/web/cockpit/leon-cockpit.css"/>').appendTo("head")

menu = $(leon.cockpit.ui.menu())
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

hideMenu = ->
	$("body").first().removeClass("leonCockpitBodyLowered")
	$("#leonCockpitMenu").hide()

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

@leon.cockpit =
	enable: ->
		console.log("Leon Cockpit enabled.")
		showMenu()

	disable: ->
		console.log("Leon Cockpit disabled.")
		hideMenu()

	displayLogMessage: (message) ->
		date = new Date()
		clearInterval(logMessageTimer)
		hideLogMessage = -> logLabel().text("")
		logMessageTimer = setTimeout(hideLogMessage, 5000)
		logLabel().text(getCurrentTime() + " " + message)

#----------------------------------------------------------
# Init
#----------------------------------------------------------

$ => @leon.cockpit.enable()

