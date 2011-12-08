
leon.comet = (function() {

    var pollTimer; // check for new data, connection state, ...

    var connectionCheckTimer; // check if we have an active connection

    var disconnectTimer; // force a disconnect

    var http;
    var prevDataLength = 0;
    var nextLine = 0;

    var messageMarker = " $$$MESSAGE$$$"; // leading space required

    var pageId = undefined; // gets defined during the first connect(...) call

    var isCometActive = false;

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
            if (!isCometActive) {
                leon.debug("handleResponse called without active comet. Ignoring request.");
                //return; // TODO should be required
            }
            leon.debug("Http readyState: " + http.readyState + "; Http status: " + http.status);

            if (http.readyState != 4 && http.readyState != 3)
                return;
            if (http.readyState == 3 && http.status != 200)
                return;
            if (http.readyState == 4 && http.status != 200) {
                leon.debug("Server connection lost.");
                leon.disconnect();
            }
            // In konqueror http.responseText is sometimes null here...
            if (http.responseText === null) {
                return;
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
                            leon.comet.handleEvent(message.topicId, dataParsed);
                        }
                    }
                }
            }

            if (http.readyState == 4 && prevDataLength == http.responseText.length) {
                leon.comet.start(pageId, true);
            }
            
        },

        connect: function(url) {
            // reset
            prevDataLength = 0;
            nextLine = 0;

            http = leon.comet.createRequestObject();
            http.open('get', url);
            http.onreadystatechange = leon.comet.handleResponse;
            http.send(null);
            pollTimer = setInterval(leon.comet.handleResponse, 5 * 1000);
            isCometActive = true;
        },

        disconnect: function() {
            isCometActive = false;
            leon.debug("Disconnect comet connection.");
            clearInterval(pollTimer);
            clearInterval(disconnectTimer);
            http.abort();
        },

        stopConnectionCheck: function() {
            clearInterval(connectionCheckTimer);
        },

        start: function(id, force) {
            if (!isCometActive || force === true) {
                leon.debug("leon.comet.start() -> starting....");
                if (pageId == undefined) {
                    console.log("pageId not yet defined. Using value: " + id)
                    pageId = id;
                }

                clearInterval(connectionCheckTimer);
                clearInterval(pollTimer);
                clearInterval(disconnectTimer);

                var url = leon.contextPath + "/leon/comet/connect" + "?pageId=" + pageId;
                leon.comet.connect(url);

                connectionCheckTimer = setInterval(function() { leon.comet.start(); }, 1 * 1000);
                disconnectTimer = setTimeout(function() { leon.comet.disconnect(); }, 10 * 1000);
            } else {
                leon.debug("leon.comet.start() -> already connected.");
            }
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
