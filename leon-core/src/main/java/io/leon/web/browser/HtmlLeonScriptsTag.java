package io.leon.web.browser;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.leon.web.htmltagsprocessor.LeonTagRewriter;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class HtmlLeonScriptsTag implements LeonTagRewriter {

    private Provider<HttpServletRequest> requestProvider;

    @Inject
    public HtmlLeonScriptsTag(Provider<HttpServletRequest> requestProvider) {
        this.requestProvider = requestProvider;
    }

    @Override
    public void process(String referrer, Source in, OutputDocument out) {
        String contextPath = requestProvider.get().getContextPath();

        List<StartTag> includeTags = in.getAllStartTags("leon-scripts");
        if (includeTags.size() == 0) {
            return;
        }
        for (StartTag tag : includeTags) {
            String s = "<script type=\"text/javascript\" src=\"" + contextPath + "/leon/leon.js\"></script>";
            out.replace(tag, s);
        }
    }
}
