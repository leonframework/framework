package io.leon.web.browser.selenium.ajax.errordialog;


public class AjaxService {

    public String call(boolean throwError) {
        if (throwError) {
            throw new RuntimeException("throwError==true");
        }
        return "throwError==false";
    }
}
