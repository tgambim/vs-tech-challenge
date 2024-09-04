package util.testClasses;

import com.gambim.framework.annotation.*;
import com.gambim.framework.exception.ValidationException;
import com.gambim.framework.routing.RequestMethod;
import java.util.Map;

@Controller(basePath = "/test")
public class TestController {

    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        return "get response";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/authenticated")
    @Authenticated
    public String authenticated() {
        return "authenticated response";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/parameters")
    public String getWithParams(
            @QueryParam(name = "param1") Integer param1, @QueryParam(name = "param2", optional = true) Integer param2) {
        return String.format("get with params response %d %d", param1, param2);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/post-body")
    public String post(@Body Map<String, String> body) {
        return String.format("post with body response %s", body.toString());
    }

    @RequestMapping(method = RequestMethod.POST)
    public String post() {
        return "post response";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/api-exception")
    public String apiException() {
        throw new ValidationException("api exception", Map.of());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/generic-exception")
    public String genericException() throws Exception {
        throw new Exception("internal error");
    }
}
