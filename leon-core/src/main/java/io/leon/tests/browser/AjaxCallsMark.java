package io.leon.tests.browser;

public class AjaxCallsMark extends AbstractAsyncMark {

    public AjaxCallsMark(LeonBrowserTester leonBrowserTester) {
        super(leonBrowserTester);
    }

    @Override
    public int getCount() {
        return getLeonBrowserTester().getAjaxCallsCount();
    }

}
