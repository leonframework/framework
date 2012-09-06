
getLeon().comet = (function() {

    var pollTimer; // check for new data, connection state, ...

    var disconnectTimer; // force a disconnect

    var socket;
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

        openSocket: function(url) {
            var request = {
                url: url,
                logLevel: "debug",
                contentType: 'application/json',
                transport: 'websocket',
                fallbackTransport: 'long-polling',
                executeCallbackBeforeReconnect: 'true'
            };

            request.onReconnect = function(request, response) {
                console.log("onReconnect");
                request.url = getLeon().contextPath + "/leon/comet/connect" + "?clientId=" + getLeon().comet.clientId + "&lastMessageId=" + lastMessageId;
            }

            request.onMessage = function(response) {
                console.log("onMessage called");

                var responseBody = response.responseBody;
                console.log("responseBody: " + responseBody);
                var message = JSON.parse(responseBody);
                var dataParsed = JSON.parse(message.data);

                lastMessageId = message.messageId;
                console.log("lastMessageId: " + lastMessageId);

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
            };

            socket = $.atmosphere.subscribe(request);
        },

        isCometActive: function() {
            return typeof socket != 'undefined';
        },

        connect: function() {
            if (getLeon().comet.isCometActive()) {
                return;
            }

            var url = getLeon().contextPath + "/leon/comet/connect" + "?clientId=" + getLeon().comet.clientId + "&lastMessageId=" + lastMessageId;
            getLeon().comet.openSocket(url);
        },

        subscribeTopic: function(topicId, handler) {
            getLeon().comet.addHandler(topicId, handler);
            getLeon().comet.updateFilter(topicId);
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
	            }, function() {
	                getLeon().comet.connect();
	            });
            } else {
                // add subscription
	            jQuery.get(url, {
	                clientId: getLeon().comet.clientId,
	                topicId: topicId
	            }, function() {
	                getLeon().comet.connect();
	            });
            }
        }
    }

})();
