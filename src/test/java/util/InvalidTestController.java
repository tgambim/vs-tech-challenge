package util;

import com.gambim.framework.annotation.*;
import com.gambim.framework.routing.RequestMethod;

@Controller(basePath = "/test-invalid")
public class InvalidTestController {

    @RequestMapping(method = RequestMethod.GET)
    public String get(Integer parameterWithoutAnnotation) {
        return "get response";
    }
}
