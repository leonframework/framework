package io.leon.web.ajax.browser.errordialog;

import io.leon.AbstractLeonConfiguration;

public class Config extends AbstractLeonConfiguration {

    @Override
    public void config() {
        browser("ajaxService").linksToServer(AjaxService.class);
    }

}
