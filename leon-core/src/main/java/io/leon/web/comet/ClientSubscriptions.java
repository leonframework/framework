package io.leon.web.comet;

import java.util.List;

public interface ClientSubscriptions {

    public List<? extends ClientSubscriptionInformation> getAllClientSubscriptions();

}
