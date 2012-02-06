package io.leon.tests.browser;

public class CometCallsMark extends AbstractAsyncMark {

    public CometCallsMark(LeonBrowserTester leonBrowserTester) {
        super(leonBrowserTester);
    }

    @Override
    public int getCount() {
        return getLeonBrowserTester().getCometCallsCount();
    }

}
