
leon.comet = (function() {

    var pollTimer;
    var http;
    var prevDataLength = 0;
    var nextLine = 0;

    var messageMarker = " $$$MESSAGE$$$"; // leading space required
 
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

        handleBrowserObjectMethodCall: function(objectName, methodName, args) {
            //var obj = eval(objectName);
            var obj = window[objectName];
            var method = obj[methodName];
            method.apply(method, args);
        },

        handleResponse: function() {
            //console.log("handleResponse: readyState: " + http.readyState + " status: " + http.status)

            if (http.readyState != 4 && http.readyState != 3)
                return;
            if (http.readyState == 3 && http.status != 200)
                return;
            if (http.readyState == 4 && http.status != 200) {
                leon.comet.connect();
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
                        if (json.type === "browserObjectMethodCall") {
                            leon.comet.handleBrowserObjectMethodCall(json.object, json.method, json.args);
                        }
                    }
                }
            }

            if (http.readyState == 4 && prevDataLength == http.responseText.length) {
                leon.comet.connect();
            }
        },

        start: function(url) {
                // reset
                prevDataLength = 0;
                nextLine = 0;

                http = this.createRequestObject();
                http.open('get', url);
                http.onreadystatechange = this.handleResponse;
                http.send(null);
                pollTimer = setInterval(this.handleResponse, 1 * 1000);
        },

        connect: function() {
            clearInterval(pollTimer);
            var url = "/leon/registerPage" + "?pageId=" + leon.pageId + "&uplink=true";
            this.start(url);
        }
    }

})();
