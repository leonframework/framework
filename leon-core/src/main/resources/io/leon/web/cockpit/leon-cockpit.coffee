
#----------------------------------------------------------
# UI elements / CSS
#----------------------------------------------------------

$('<link rel="stylesheet" href="/io/leon/web/cockpit/leon-cockpit.css"/>').appendTo("head")

menu = $(leon.cockpit.ui.menu())

#----------------------------------------------------------
# Internal functions
#----------------------------------------------------------

showMenu = ->
	$("body").first().addClass("leonCockpitBodyLowered")
	$("body").prepend(menu)

hideMenu = ->
	$("body").first().removeClass("leonCockpitBodyLowered")
	$("#leonCockpitMenu").hide()

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

#----------------------------------------------------------
# Init
#----------------------------------------------------------

$ =>
	@leon.cockpit.enable()

