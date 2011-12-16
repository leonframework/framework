
@timestamp = new Date().getTime()

# Receive device orientation for all clients
@handleMessage = (message) =>
  data = message.data
  clientId = message.clientId
  tiltLR = data.tiltLR
  tiltFB = data.tiltFB

  $("#container").append("<img id='#{clientId}' src='#{leon.contextPath}/leon.png' />") if $("#" + clientId).size() == 0

  $("#"+clientId).css("-webkit-transform", "rotate(#{tiltLR}deg) rotate3d(1,0,0, #{(tiltFB * -1)}deg)")
  $("#"+clientId).css("-moz-transform", "rotate(#{tiltLR}deg)")
  $("#"+clientId).css("transform", "rotate(#{tiltLR}deg) rotate3d(1,0,0, #{(tiltFB * -1)}deg)")


# Send device orientation
@handleOrientationChanged = (event) =>
  if new Date().getTime() - @timestamp < 250
    return

  @timestamp = new Date().getTime()

  tiltLR = event.gamma
  tiltFB = event.beta
  dir = event.alpha

  server.leoncomet("publishOrientation")( {tiltLR: tiltLR, tiltFB: tiltFB, dir: dir} )


@handleMozOrientationChanged = (event) =>
  tiltLR = event.x * 90
  tiltFB = event.y * -90
  dir = null

  server.leoncomet("publishOrientation")( {tiltLR: tiltLR, tiltFB: tiltFB, dir: dir} )


# Setup
if window.DeviceOrientationEvent
  window.addEventListener("deviceorientation", handleOrientationChanged, true)
  window.addEventListener("MozOrientation", handleMozOrientationChanged, true)
else
  alert("Sorry, your browser and/or device doesn't support device orientation.")
