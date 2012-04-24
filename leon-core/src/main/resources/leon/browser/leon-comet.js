
getLeon().comet = (function() {

    var pollTimer; // check for new data, connection state, ...

    var disconnectTimer; // force a disconnect

    var http;
    var prevDataLength = 0;
    var nextLine = 0;

    var messageMarker = "$$$MESSAGE$$$";

    var lastMessageId = -1;

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
            handlerFns[topicId].map(function(fn) {
                fn(data);
            });
        },

        handleResponse: function() {
            //getLeon().debug("Http readyState: " + http.readyState + "; Http status: " + http.status + "; done reading: " + (prevDataLength == http.responseText.length));
            // Documentation
            // --------------------------------------------
            // readyState:
            // 0 UNINITIALIZED open() has not been called yet.
            // 1 LOADING send() has not been called yet.
            // 2 LOADED send() has been called, headers and status are available.
            // 3 INTERACTIVE Downloading, responseText holds the partial data.
            // 4 COMPLETED Finished with all operations.

            if (http.readyState != 4 && http.readyState != 3)
                return;
            if (http.readyState == 3 && http.status != 200)
                return;

            getLeon().comet.readBuffer();
        },

        readBuffer: function() {
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
                        lastMessageId = message.messageId;
                        try {
                            getLeon().log("Comet handler called")
                            getLeon().comet.handleEvent(message.topicName, dataParsed);
                        } catch (err) {
                            getLeon().log("Comet handler ERROR");
                            if (getLeon().deploymentMode === "development") {
                                console.log(err.description);
                                alert(err);
                            }
                        }
                    }
                }
            }
        },

        openSocket: function(url) {
            // reset
            prevDataLength = 0;
            nextLine = 0;

            http = getLeon().comet.createRequestObject();
            http.open('get', url);
            http.onreadystatechange = getLeon().comet.handleResponse;
            pollTimer = setInterval(getLeon().comet.handleResponse, 5 * 1000);
            http.send(null);
        },

        isCometActive: function() {
            return http && http.readyState != 4 && http.readyState != 0;
        },

        connect: function(id) {
            if (getLeon().comet.isCometActive()) {
                return;
            }

            clearInterval(pollTimer);
            clearInterval(disconnectTimer);

            var url = getLeon().contextPath + "/leon/comet/connect" + "?clientId=" + getLeon().comet.clientId + "&lastMessageId=" + lastMessageId;
            getLeon().comet.openSocket(url);

            // check every 2 seconds that we have a connection
            (function connectionCheck() {
               setTimeout(function() {
                  getLeon().comet.connect();
                  connectionCheck();
              }, 2000);
            })();

            // close and open the connection every 30 seconds
            disconnectTimer = setTimeout(function() {
                http.abort();
            }, 30 * 1000);
        },

        subscribeTopic: function(topicId, handler) {
            getLeon().comet.connect();
            getLeon().comet.addHandler(topicId, handler);

            (function waitForActiveConnection() {
                if (http && http.readyState == 3) {
                    getLeon().comet.updateFilter(topicId);
                } else {
                    setTimeout(function() { waitForActiveConnection() }, 500);
                }
            })();
        },

        addHandler: function(topicId, handlerFn) {
            if (handlerFns.hasOwnProperty(topicId)) {
                handlerFns[topicId].push(handlerFn);
            } else {
                handlerFns[topicId] = [handlerFn];
            }
        },

        updateFilter: function(topicId, key, value) {
            var url = getLeon().contextPath + "/leon/comet/updateFilter";
            if (key) {
                // update filter
	            jQuery.get(url, {
	                clientId: getLeon().comet.clientId,
	                topicId: topicId,
	                key: key,
	                value: value
	            });
            } else {
                // add subscription
	            jQuery.get(url, {
	                clientId: getLeon().comet.clientId,
	                topicId: topicId
	            });
            }
        }
    }

})();
