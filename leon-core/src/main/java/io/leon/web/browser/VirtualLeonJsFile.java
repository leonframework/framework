package io.leon.web.browser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.leon.resourceloading.Resource;
import io.leon.resourceloading.ResourceLoader;
import io.leon.utils.GuiceUtils;
import io.leon.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class VirtualLeonJsFile extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ResourceLoader resourceLoader;

    private final List<VirtualLeonJsFileContribution> contributions;

    @Inject
    public VirtualLeonJsFile(Injector injector, ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

        contributions = Lists.newLinkedList();
        for (Binding<VirtualLeonJsFileContribution> b :
                GuiceUtils.getByType(injector, VirtualLeonJsFileContribution.class)) {
            contributions.add(b.getProvider().get());
        }
    }

    private void writeResource(Writer out, String name) throws IOException {
        Resource resource = resourceLoader.getResource(name);
        out.write(ResourceUtils.inputStreamToString(resource.getInputStream()));
    }

    private void writeLine(Writer out, String string) throws IOException {
        out.write(string);
        out.write("\n");
    }

    private Map<String, String> simplifyRequestMap(Map<?, ?> map) {
        Map<String, String> simpleMap = Maps.newHashMap();
        for (Object oKey : map.keySet()) {
            String key = (String) oKey;
            String[] values = (String[]) map.get(oKey);
            simpleMap.put(key, values[0]);
        }
        return simpleMap;
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/javascript");
        Writer out = new BufferedWriter(res.getWriter());

        // static content
        writeLine(out, "\"use strict\";");

        String env = req.getParameter("env");
        if ("desktop".equals(env) || null == env) {
            writeResource(out, "/leon/browser/jquery-1.6.2.js");
            writeResource(out, "/leon/browser/jquery.atmosphere.js");
            writeResource(out, "/leon/browser/leon-browser.js");
            writeResource(out, "/leon/browser/leon-shared.js");
            writeResource(out, "/leon/browser/leon-comet.js");
        } else if ("mobile".equals(env)) {
            //writeResource(out, "/leon/browser/jquery-1.6.2.js")
            //writeResource(out, "/leon/browser/jquery.mobile-1.0b3.js")
            //writeResource(out, "/leon/browser/jquery-mobile-angular-adapter-1.0.2.js")
            //writeResource(out, "/leon/browser/leon-browser.js")
            //writeResource(out, "/leon/browser/leon-shared.js")
            //writeResource(out, "/leon/browser/leon-comet.js")
        } else {
            throw new RuntimeException("You can add either '?env=desktop' (default) or '?env=mobile' when loading leon.js.");
        }

        // dynamic content
        Map<String, String> requestMap = simplifyRequestMap(req.getParameterMap());
        for (VirtualLeonJsFileContribution contribution : contributions) {
            try {
                out.write(contribution.content(requestMap));
                out.write("\n");
            } catch (Exception e) {
                logger.error("Error while processing VirtualLeonJsFileContribution", e);
            }
        }

        out.close();
    }
}
