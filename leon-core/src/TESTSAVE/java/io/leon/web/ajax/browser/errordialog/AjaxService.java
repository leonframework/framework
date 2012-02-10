package io.leon.web.ajax.browser.errordialog;


public class AjaxService {

    public String call(boolean throwError) {
        if (throwError) {
            throw new RuntimeException("throwError==true");
        }
        return "throwError==false";
    }
}
