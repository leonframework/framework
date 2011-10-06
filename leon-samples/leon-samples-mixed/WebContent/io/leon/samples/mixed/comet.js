
var comet = (function() {

    var count = 0;

    return {
        startPing: function() {

            var browserPing = browser.comet("ping")

            var runnableImpl = {
                run: function() {
                    while (true) {
                        java.lang.System.out.println("PING:" + count);
                        browserPing(count);
                        count += 1;
                        java.lang.Thread.sleep(1000);
                    }
                }
            }
            var runnableObj = new java.lang.Runnable(runnableImpl);
            var thread = new java.lang.Thread(runnableObj);
            thread.start();
        }
    };

})();
