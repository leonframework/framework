package io.leon.web.angular;

import io.leon.web.browser.VirtualLeonJsFileContribution;

import java.util.Map;

public class AngularAutoCompileLeonJsContribution implements VirtualLeonJsFileContribution {

    @Override
    public String content(Map<String, String> params) {
        if ("false".equals(params.get("angularAutoCompile"))) {
            return "";
        } else {
            return "angular.element(document).ready(function(){" +
                    "leon.angularDocument = angular.compile(document)();" +
                    "});";
        }
    }

}
