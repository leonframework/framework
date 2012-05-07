package io.leon.web.browser;

import io.leon.web.htmltagsprocessor.LeonTagRewriter;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import java.util.List;

public class HtmlLeonScriptsTag implements LeonTagRewriter {

    @Override
    public void process(String referrer, Source in, OutputDocument out) {
        List<StartTag> includeTags = in.getAllStartTags("leon-scripts");
        if (includeTags.size() == 0) {
            return;
        }
        for (StartTag tag : includeTags) {
            String s = "<script type=\"text/javascript\" src=\"/leon/leon.js\"></script>";
            out.replace(tag, s);
        }
    }
}
