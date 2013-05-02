package io.leon;

import com.google.inject.AbstractModule;
import io.leon.config.ConfigModule;
import io.leon.gson.GsonModule;
import io.leon.javascript.LeonJavaScriptModule;
import io.leon.web.ajax.AjaxModule;
import io.leon.web.browser.BrowserModule;
import io.leon.web.comet.CometModule;
import io.leon.web.resources.WebResourcesModule;

public class DefaultWebAppGroupingModule extends AbstractModule {

    private final ConfigModule configModule = new ConfigModule();

    public DefaultWebAppGroupingModule init() {
        configModule.init();
        return this;
    }

    @Override
    protected void configure() {
        install(configModule);
        install(new GsonModule());
        install(new LeonJavaScriptModule());
        install(new AjaxModule());
        install(new CometModule());
        install(new BrowserModule());

        install(new WebResourcesModule()); // must be at the last position!
    }
}
