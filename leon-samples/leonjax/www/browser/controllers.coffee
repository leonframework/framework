@LeonJaxCtrl = ($xhr) ->
  @messages = []
  @rooms = [{name: "Rom"}, {name:"Sydney"}]
  @user =
    name: ""
    room: ""

  @$watch("user.room", "updateFilter()")

  @updateFilter = ->
    leon.comet.updateFilter("leonjax.room.messages", "room", @user.room.name) if @user.room

  @postMessage = (message) ->
    server.leonJaxService("postMessage")(@user.name, @user.room.name, message)

@handleMessage = (msg) ->
  $("#messages").prepend("<div class='message'>" + msg + "</div>");

