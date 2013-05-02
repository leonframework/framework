package io.leon.web.browser;

import com.google.inject.Inject;
import io.leon.resourceloading.Resource;
import io.leon.resourceloading.ResourceLoader;
import io.leon.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

public class VirtualJsFile extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ResourceLoader resourceLoader;

    private final List<VirtualJsFileContribution> contributions = new LinkedList<VirtualJsFileContribution>();


    @Inject
    public VirtualJsFile(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private void writeResource(Writer out, String name) throws IOException {
        Resource resource = resourceLoader.getResource(name);
        out.write(ResourceUtils.inputStreamToString(resource.getInputStream()));
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/javascript");
        Writer out = new BufferedWriter(res.getWriter());

        writeResource(out, "/leon/browser/jquery-1.6.2.js");
        writeResource(out, "/leon/browser/leon-browser.js");
        writeResource(out, "/leon/browser/leon-shared.js");
        writeResource(out, "/leon/browser/leon-comet.js");

        for (VirtualJsFileContribution contribution : contributions) {
            try {
                out.write(contribution.getContent());
                out.write("\n");
            } catch (Exception e) {
                logger.error("Error while processing VirtualJsFileContribution", e);
            }
        }
        out.close();
    }
}
