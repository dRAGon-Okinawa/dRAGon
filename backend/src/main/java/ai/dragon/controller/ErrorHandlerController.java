package ai.dragon.controller;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ErrorHandlerController implements ErrorController {
    // We are using a SPA (Single Page Application) so we need to output the
    // index.html file when an error occurs :
    @RequestMapping("/error")
    public @ResponseBody byte[] getSpaContent() throws IOException {
        InputStream in = getClass().getResourceAsStream("/static/index.html");
        if (in == null) {
            throw new IOException("index.html not found");
        }
        return IOUtils.toByteArray(in);
    }

    public String getErrorPath() {
        return "/error";
    }
}
