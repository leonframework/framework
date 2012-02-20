package io.leon.web.browser.ajax.basicjs;

import io.leon.AbstractLeonConfiguration;
import io.leon.web.ajax.AjaxBinder;

public class AjaxTestModule extends AbstractLeonConfiguration {

    @Override
    public void config() {
        loadFile(getClass().getPackage().getName().replace(".", "/") + "/AjaxService.js");
        exposeUrl(".*");
        AjaxBinder ab = new AjaxBinder(super.binder());
        ab.exposeJavaScript("/ajaxService", "AjaxService");
    }

}
