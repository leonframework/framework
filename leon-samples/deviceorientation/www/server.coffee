HttpSession = Packages.javax.servlet.http.HttpSession

@leoncomet =
    publishOrientation: (data) ->
        session = leon.inject(HttpSession)
        sessionId = session.id

        Packages.java.lang.System.out.println(sessionId)

        leon.publishMessage("leoncomet.orientation", {}, { clientId: sessionId, data: data })