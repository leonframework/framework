
@DummyAppCtrl = ->
    @text = "Hello World!"
    @reversed = ""

    @reverse = ->
        leon.service("/ajaxService", "method1").call @text, (@reversed) =>
