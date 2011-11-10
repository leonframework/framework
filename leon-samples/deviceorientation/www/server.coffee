HttpSession = Packages.javax.servlet.http.HttpSession

@leoncomet =
    publishOrientation: (data) ->
        session = leon.inject(HttpSession)
        leon.publishMessage("leoncomet.orientation", {}, { clientId: session.id, data: data })
