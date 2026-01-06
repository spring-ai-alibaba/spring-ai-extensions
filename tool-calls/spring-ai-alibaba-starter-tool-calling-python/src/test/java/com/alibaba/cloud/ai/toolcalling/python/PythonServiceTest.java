/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.toolcalling.python;

import com.alibaba.cloud.ai.toolcalling.python.PythonService.Request;
import com.alibaba.cloud.ai.toolcalling.python.PythonService.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {PythonServiceAutoConfiguration.class})
@DisplayName("Python Tool Service Test")
public class PythonServiceTest {

    @Autowired
    private PythonService pythonService;

    private static final Logger log = Logger.getLogger(PythonServiceTest.class.getName());

    @Test
    @DisplayName("Test simple arithmetic calculation")
    public void testSimpleArithmetic() {
        Request request = new Request("2 + 3");
        Response response = pythonService.apply(request, null);

        assertNotNull(response);
        assertNotNull(response.result());
        assertTrue(response.result().contains("5"), "Result should contain 5, but got: " + response.result());
        log.info("Arithmetic result: " + response.result());
    }

    @Test
    @DisplayName("Test string operations")
    public void testStringOperations() {
        Request request = new Request("'Hello, ' + 'World'");
        Response response = pythonService.apply(request, null);

        assertNotNull(response);
        assertNotNull(response.result());
        assertTrue(response.result().contains("Hello, World"),
                "Result should contain 'Hello, World', but got: " + response.result());
        log.info("String operation result: " + response.result());
    }

    @Test
    @DisplayName("Test list operations")
    public void testListOperations() {
        Request request = new Request("[1, 2, 3, 4, 5]");
        Response response = pythonService.apply(request, null);

        assertNotNull(response);
        assertNotNull(response.result());
        log.info("List operation result: " + response.result());
    }

    @Test
    @DisplayName("Test boolean expressions")
    public void testBooleanExpressions() {
        Request request = new Request("5 > 3");
        Response response = pythonService.apply(request, null);

        assertNotNull(response);
        assertNotNull(response.result());
        assertTrue(response.result().toLowerCase().contains("true"),
                "Result should be true, but got: " + response.result());
        log.info("Boolean expression result: " + response.result());
    }

    @Test
    @DisplayName("Test empty code error handling")
    public void testEmptyCode() {
        Request request = new Request("");
        Response response = pythonService.apply(request, null);

        assertNotNull(response);
        assertNotNull(response.result());
        assertTrue(response.result().contains("Error"), "Should return error for empty code");
        log.info("Empty code result: " + response.result());
    }

    @Test
    @DisplayName("Test null code error handling")
    public void testNullCode() {
        Request request = new Request(null);
        Response response = pythonService.apply(request, null);

        assertNotNull(response);
        assertNotNull(response.result());
        assertTrue(response.result().contains("Error"), "Should return error for null code");
        log.info("Null code result: " + response.result());
    }

    @Test
    @DisplayName("Test invalid Python syntax error handling")
    public void testInvalidSyntax() {
        Request request = new Request("def invalid syntax");
        Response response = pythonService.apply(request, null);

        assertNotNull(response);
        assertNotNull(response.result());
        assertTrue(response.result().contains("Error"), "Should return error for invalid syntax");
        log.info("Invalid syntax result: " + response.result());
    }

    @Test
    @DisplayName("Test complex calculation")
    public void testComplexCalculation() {
        Request request = new Request("(10 + 20) * 3 - 5");
        Response response = pythonService.apply(request, null);

        assertNotNull(response);
        assertNotNull(response.result());
        assertTrue(response.result().contains("85"), "Result should be 85, but got: " + response.result());
        log.info("Complex calculation result: " + response.result());
    }

    @Test
    @DisplayName("Test with default constructor")
    public void testDefaultConstructor() {
        PythonService defaultService = new PythonService();
        Request request = new Request("1 + 1");
        Response response = defaultService.apply(request, null);

        assertNotNull(response);
        assertNotNull(response.result());
        assertTrue(response.result().contains("2"), "Result should contain 2, but got: " + response.result());
        log.info("Default constructor test result: " + response.result());
    }

    @Test
    @DisplayName("Test with custom properties")
    public void testCustomProperties() {
        PythonProperties customProps = new PythonProperties();
        customProps.setEnabled(true);
        customProps.getEngine().setWarnInterpreterOnly(false);
        customProps.getContext().setAllowHostAccess(true);

        PythonService customService = new PythonService(customProps);
        Request request = new Request("3 * 7");
        Response response = customService.apply(request, null);

        assertNotNull(response);
        assertNotNull(response.result());
        assertTrue(response.result().contains("21"), "Result should contain 21, but got: " + response.result());
        log.info("Custom properties test result: " + response.result());
    }

}
