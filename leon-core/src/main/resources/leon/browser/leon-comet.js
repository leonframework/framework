
leon.comet = (function() {

    var pollTimer;
    var http;
    var prevDataLength = 0;
    var nextLine = 0;

    var messageMarker = " $$$MESSAGE$$$"; // leading space required

    var pageId = undefined;

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
            //console.log("handleResponse: readyState: " + http.readyState + " status: " + http.status)

            if (http.readyState != 4 && http.readyState != 3)
                return;
            if (http.readyState == 3 && http.status != 200)
                return;
            if (http.readyState == 4 && http.status != 200) {
                leon.comet.connect(pageId, true);
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
                        var message = line.substring(messageMarker.length, line.length);
                        var json = JSON.parse(message);
                        if (json.type === "publishedEvent") {
                            leon.comet.handleEvent(json.topicId, json.data);
                        }
                    }
                }
            }

            if (http.readyState == 4 && prevDataLength == http.responseText.length) {
                leon.comet.connect(pageId, true);
            }
        },

        start: function(url) {
            // reset
            prevDataLength = 0;
            nextLine = 0;

            http = leon.comet.createRequestObject();
            http.open('get', url);
            http.onreadystatechange = leon.comet.handleResponse;
            http.send(null);
            pollTimer = setInterval(leon.comet.handleResponse, 1 * 1000);
            isCometActive = true;
        },

        connect: function(id, force) {
            if (!isCometActive || force === true) {
                pageId = id;
                clearInterval(pollTimer);
                var url = "/leon/comet/connect" + "?pageId=" + pageId;
                leon.comet.start(url);
            }
        },

        addHandler: function(topicId, handlerFn) {
            handlerFns[topicId] = handlerFn;
        },

        updateFilter: function(topicId, key, value) {
            jQuery.get("/leon/comet/updateFilter", {
                pageId: pageId,
                topicId: topicId,
                key: key,
                value: value
            });
        }
    }

})();
