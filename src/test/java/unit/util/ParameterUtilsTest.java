package unit.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gambim.framework.routing.util.ParameterUtils;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ParameterUtilsTest {

    @Test
    void testGetQueryParameterWithStringParameter() {
        Map<String, String> params = Map.of("param1", "test");

        Object result =
                assertDoesNotThrow(() -> ParameterUtils.getQueryParameter(params, String.class, "param1", false));

        assertEquals("test", result);
    }

    @Test
    void testGetQueryParameterWithIntegerParameter() {
        Map<String, String> params = Map.of("param1", "1");

        Object result =
                assertDoesNotThrow(() -> ParameterUtils.getQueryParameter(params, Integer.class, "param1", false));

        assertEquals(1, result);
    }

    @Test
    void testGetQueryParameterWithLongParameter() {
        Map<String, String> params = Map.of("param1", "1");

        Object result = assertDoesNotThrow(() -> ParameterUtils.getQueryParameter(params, Long.class, "param1", false));

        assertEquals(1L, result);
    }

    @Test
    void testGetQueryParameterWithShortParameter() {
        Map<String, String> params = Map.of("param1", "1");

        Object result =
                assertDoesNotThrow(() -> ParameterUtils.getQueryParameter(params, Short.class, "param1", false));

        assertEquals(Short.valueOf("1"), result);
    }

    @Test
    void testGetQueryParameterWithByteParameter() {
        Map<String, String> params = Map.of("param1", "1");

        Object result = assertDoesNotThrow(() -> ParameterUtils.getQueryParameter(params, Byte.class, "param1", false));

        assertEquals(Byte.valueOf("1"), result);
    }

    @Test
    void testGetQueryParameterWithBooleanParameter() {
        Map<String, String> params = Map.of("param1", "true");

        Object result =
                assertDoesNotThrow(() -> ParameterUtils.getQueryParameter(params, Boolean.class, "param1", false));

        assertEquals(true, result);
    }

    @Test
    void testGetQueryParameterWithDoubleParameter() {
        Map<String, String> params = Map.of("param1", "1.5");

        Object result =
                assertDoesNotThrow(() -> ParameterUtils.getQueryParameter(params, Double.class, "param1", false));

        assertEquals(1.5d, result);
    }

    @Test
    void testGetQueryParameterWithFloatParameter() {
        Map<String, String> params = Map.of("param1", "1.5");

        Object result =
                assertDoesNotThrow(() -> ParameterUtils.getQueryParameter(params, Float.class, "param1", false));

        assertEquals(1.5f, result);
    }
}
