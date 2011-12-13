
leon.comet = (function() {

    var pollTimer; // check for new data, connection state, ...

    var connectionCheckTimer; // check if we have an active connection

    var disconnectTimer; // force a disconnect

    var http;
    var prevDataLength = 0;
    var nextLine = 0;

    var messageMarker = " $$$MESSAGE$$$"; // leading space required

    var pageId = undefined; // gets defined during the first connect(...) call

    var cometActive = false;

    var handlerFns = {};
 
    return {

        createRequestObject: function() {
            var ro;
            if (window.XMLHttpRequest) {
                    ro = new XMLHttpRequest();
            } else {
                    ro = new ActiveXObject("Microsoft.XMLHTTP");
            }
            if (!ro)
                    debug("Couldn't start XMLHttpRequest object");
            return ro;
        },

        handleEvent: function(topicId, data) {
            handlerFns[topicId](data);
        },

        handleResponse: function() {
            //leon.debug("Http readyState: " + http.readyState + "; Http status: " + http.status + "; done reading: " + (prevDataLength == http.responseText.length));
            if (http.readyState != 4 && http.readyState != 3)
                return false;
            if (http.readyState == 3 && http.status != 200)
                return false;
            if (http.readyState == 4 && http.status != 200) {
                leon.debug("Server connection lost.");
                leon.comet.disconnect();
                //leon.comet.connect();
            }

            if (http.readyState == 4 && prevDataLength == http.responseText.length) {
                leon.comet.start(pageId, true);
            }

            return leon.comet.readBuffer();
        },

        readBuffer: function() {
            // In konqueror http.responseText is sometimes null here...
            if (http.responseText === null) {
                return true;
            }

            while (prevDataLength != http.responseText.length) {
                if (http.readyState == 4  && prevDataLength == http.responseText.length)
                    break;

                prevDataLength = http.responseText.length;
                var response = http.responseText.substring(nextLine);

                var lines = response.split('\n');
                nextLine = nextLine + response.lastIndexOf('\n') + 1;
                if (response[response.length-1] != '\n')
                    lines.pop();

                for (var i = 0; i < lines.length; i++) {
                    var line = lines[i];
                    if (line.substring(0, messageMarker.length) === messageMarker) {
                        var messageRaw = line.substring(messageMarker.length, line.length);
                        var message = JSON.parse(messageRaw);
                        var dataParsed = JSON.parse(message.data);

                        if (message.type === "publishedEvent") {
                            leon.comet.handleEvent(message.topicName, dataParsed);
                        }
                    }
                }
            }
            return prevDataLength == http.responseText.length;
        },

        openSocket: function(url) {
            // reset
            prevDataLength = 0;
            nextLine = 0;

            http = leon.comet.createRequestObject();
            http.open('get', url);
            http.onreadystatechange = leon.comet.handleResponse;
            pollTimer = setInterval(leon.comet.handleResponse, 5 * 1000);

            connectionCheckTimer = setInterval(function() {

                // TODO we need to stop the timer to avoid double connection attempts

                leon.comet.connect();
            }, 1 * 1000);

            cometActive = true;
            http.send(null);
        },

        isCometActive: function() {
            return cometActive; // TODO include socket in test and make sure that the cometActive variable is in sync
        },

        connect: function(id) {
            if (leon.comet.isCometActive()) {
                //leon.debug("Comet connection already active.");
                return; // already connected
            }

            //leon.debug("Starting Comet connection.");
            if (pageId == undefined) {
                // a page can only define the pageId once
                pageId = id;
            }

            clearInterval(connectionCheckTimer);
            clearInterval(pollTimer);
            clearInterval(disconnectTimer);

            var url = leon.contextPath + "/leon/comet/connect" + "?pageId=" + pageId;
            leon.comet.openSocket(url);

            // close and open the connection every 10 seconds
            disconnectTimer = setTimeout(function() {
                leon.comet.disconnect();
            }, 10 * 1000);
        },

        disconnect: function() {
            //leon.debug("Disconnect comet connection.");
            //clearInterval(connectionCheckTimer);
            clearInterval(pollTimer);
            clearInterval(disconnectTimer);

            while (!leon.comet.readBuffer()) {
                leon.debug("Reading buffer before closing connection.");
            }

            http.abort();
            cometActive = false;
        },

        addHandler: function(topicId, handlerFn) {
            handlerFns[topicId] = handlerFn;
        },

        updateFilter: function(topicId, key, value) {
            jQuery.get(leon.contextPath + "/leon/comet/updateFilter", {
                pageId: pageId,
                topicId: topicId,
                key: key,
                value: value
            });
        }
    }

})();
