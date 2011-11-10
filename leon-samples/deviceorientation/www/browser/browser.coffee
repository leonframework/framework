@timestamp = new Date().getTime();

@handleMessage = (message) =>
    data = message.data
    rotate(message.clientId, data.tiltLR, data.tiltFB, data.dir)

@handleOrientationChanged = (event) =>
    if new Date().getTime() - @timestamp < 250
        return

    @timestamp = new Date().getTime()

    tiltLR = event.gamma
    tiltFB = event.beta
    dir = event.alpha

    server.leoncomet("publishOrientation")({tiltLR: tiltLR, tiltFB: tiltFB, dir: dir}, (result) => )

@handleMozOrientationChanged = (event) =>
    tiltLR = event.x * 90
    tiltFB = event.y * -90
    dir = null

    server.leoncomet("publishOrientation")({tiltLR: tiltLR, tiltFB: tiltFB, dir: dir}, (result) => )

rotate = (clientId, tiltLR, tiltFB, dir) ->
    console.log(clientId)

    if $("#" + clientId).size() == 0
        $("#container").append("<img id='#{clientId}' src='#{leon.contextPath}/leon.png' />")

    $("#"+clientId).css("-webkit-transform", "rotate(#{tiltLR}deg) rotate3d(1,0,0, #{(tiltFB * -1)}deg)")
    $("#"+clientId).css("-moz-transform", "rotate(#{tiltLR}deg)")
    $("#"+clientId).css("transform", "rotate(#{tiltLR}deg) rotate3d(1,0,0, #{(tiltFB * -1)}deg)")


window.addEventListener("deviceorientation", handleOrientationChanged, true) if window.DeviceOrientationEvent
window.addEventListener("MozOrientation", handleMozOrientationChanged, true) if window.OrientationEvent
#http://www.html5rocks.com/en/tutorials/device/orientation/devicemotionsample.html