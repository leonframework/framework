package io.leon.web.browser.ajax.errordialog;


public class AjaxService {

    public String call(boolean throwError) {
        if (throwError) {
            throw new RuntimeException("throwError==true");
        }
        return "throwError==false";
    }
}
