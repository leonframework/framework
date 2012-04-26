package io.leon.web.comet;

import java.util.Set;

public interface ClientSubscriptionInformation {

    public String getClientId();

    public boolean hasConnection();

    public Set<String> getAllSubscribedTopics();

    public boolean hasSubscribedTopic(String topicName);

    public boolean hasFilterValue(String topicName, String filterName, String requiredFilterValue);

}
