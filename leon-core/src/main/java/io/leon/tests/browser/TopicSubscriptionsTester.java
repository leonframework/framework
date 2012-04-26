package io.leon.tests.browser;

import com.google.inject.Inject;
import io.leon.web.comet.ClientSubscriptionInformation;
import io.leon.web.comet.ClientSubscriptions;

public class TopicSubscriptionsTester {

    @Inject
    ClientSubscriptions clientSubscriptions;

    public TopicSubscriptionsTester(LeonBrowserTester leonBrowserTester) {
        leonBrowserTester.getLeonFilter().getInjector().injectMembers(this);
    }

    private boolean checkForBrowserConnection(String topicName, String filterName, String filterValue) {
        for (ClientSubscriptionInformation csi : clientSubscriptions.getAllClientSubscriptions()) {
            if (csi.hasConnection() && csi.hasSubscribedTopic(topicName)) {
                if (filterName == null) {
                    return true;
                } else if (csi.hasFilterValue(topicName, filterName, filterValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void waitForSubscription(String topicName) {
        waitForSubscription(topicName, null, null, 5);
    }

    public void waitForSubscription(String topicName, String filterName, String filterValue) {
        waitForSubscription(topicName, filterName, filterValue, 5);
    }

    public void waitForSubscription(String topicName, String filterName, String filterValue, int timeOutSeconds) {
        long startTime = System.currentTimeMillis();
        while (!checkForBrowserConnection(topicName, filterName, filterValue)) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if ((startTime + (timeOutSeconds * 1000)) < System.currentTimeMillis()) {
                throw new RuntimeException("Timeout while waiting for a browser connection ["
                        + "topic: " + topicName
                        + ", filterName: " + filterName
                        + ", filterValue: " + filterValue
                        + "]");
            }
        }
    }

}
