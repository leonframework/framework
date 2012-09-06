
getLeon().comet = (function() {

    var socket;

    var lastMessageId = -1;

    var handlerFns = {};

    function buildCometUrl() {
        return getLeon().contextPath + "/leon/comet/connect" + "?clientId=" + getLeon().comet.clientId + "&lastMessageId=" + lastMessageId;
    }

    return {

        handleEvent: function(topicId, data) {
            handlerFns[topicId].map(function(fn) {
                fn(data);
            });
        },

        openSocket: function(url) {
            var request = {
                url: url,
                contentType: 'application/json',
                transport: 'websocket',
                fallbackTransport: 'streaming',
                trackMessageLength: 'true',
                executeCallbackBeforeReconnect: 'true',
                timeout: '45'
            };

            request.onReconnect = function(request, response) {
                request.url = buildCometUrl();
            }

            request.onMessage = function(response) {
                var responseBody = response.responseBody;
                if (responseBody == null || responseBody.length == 0) {
                    return;
                }

                var message = JSON.parse(responseBody);
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

            var url = buildCometUrl();
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
