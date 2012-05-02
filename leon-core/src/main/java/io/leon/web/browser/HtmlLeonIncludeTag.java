package io.leon.web.browser;

import com.google.inject.Inject;
import io.leon.resourceloading.ResourceLoader;
import io.leon.resourceloading.ResourceUtils;
import io.leon.web.htmltagsprocessor.LeonTagRewriter;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

public class HtmlLeonIncludeTag implements LeonTagRewriter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ResourceLoader resourceLoader;

    @Inject
    public HtmlLeonIncludeTag(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void process(String referrer, Source in, OutputDocument out) {
        List<StartTag> includeTags = in.getAllStartTags("leon:include");
        if (includeTags.size() == 0) {
            return;
        }
        for (StartTag tag : includeTags) {
            String src = tag.getAttributeValue("src");

            // check for relative path
            if (!src.startsWith("/")) {
                src = referrer.substring(0, referrer.lastIndexOf("/")) + "/" + src;
            }

            logger.debug("Processing <leon:include src=\"{}\" />", src);

            InputStream inputStream = resourceLoader.getResource(src).getInputStream();
            String string = ResourceUtils.inputStreamToString(inputStream);
            out.replace(tag, string);
        }
    }
}
