HttpSession = Packages.javax.servlet.http.HttpSession

logger = leon.getLogger("deviceorientation-coffee")

@leoncomet =
    publishOrientation: (data) ->
        httpSession = leon.inject(HttpSession)
        logger.info("Session ID: " + httpSession.id)
        leon.publishMessage("leoncomet.orientation", {}, { clientId: httpSession.id, data: data })
