package io.leon.web.browser.ajax.basic;

import io.leon.AbstractLeonConfiguration;
import io.leon.web.ajax.AjaxBinder;

public class AjaxTestModule extends AbstractLeonConfiguration {

    @Override
    public void config() {
        exposeUrl(".*");
        AjaxBinder ab = new AjaxBinder(super.binder());
        ab.exposeJavaService("/ajaxService", AjaxService.class);
    }

}
