// Copyright 2008 Conan Dalton and Jean-Philippe Hallot
//
// This file is part of risible-web.
//
// risible-web is free software: you can redistribute it and/or modify
// it under the terms of version 3 of the GNU Lesser General Public License as published by
// the Free Software Foundation
//
// risible-db is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// Copies of the GNU General Public License and the GNU Lesser General Public License
// are distributed with this software, see /GPL.txt and /LGPL.txt at the
// root of this distribution.
//

package risible.core;

import freemarker.template.TemplateException;
import ognl.Ognl;
import org.junit.Test;
import org.mockito.verification.VerificationMode;
import risible.core.dispatch.Filter;
import risible.core.foo.ControllerForTesting;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class FilterTest extends TestableFilter {
    String uri;
    String url;
    String format = "";


    @Test
    public void testChainsAnythingWithADotInTheURI() throws IOException, ServletException, TemplateException, NoSuchMethodException {
        uriIs("/foo/bar/toto.action");
        urlIs("/foo/bar/toto.action");

        Filter filter = prepare(new Filter() {
            protected void runAction(HttpServletRequest request, HttpServletResponse response, long start) {
                fail();
            }
        });

        doFilter(filter);
        verify(chain, times(1)).doFilter(req, res);
    }

    @Test
    public void testRunsAnythingWithoutADotInTheURI() throws IOException, ServletException, TemplateException, NoSuchMethodException {
        uriIs("/foo/bar/toto/stuff");
        urlIs("/foo/bar/toto/stuff");
        expectDoFilterCall(never());

        Filter filter = prepare(new Filter() {
            protected void runAction(HttpServletRequest request, HttpServletResponse response, long start) {
            }
        });

        doFilter(filter);
    }

    @Test
    public void testWrapsExceptionsInServletException() throws IOException, TemplateException, NoSuchMethodException, ServletException {
        uriIs("/foo/bar/toto/stuff");
        urlIs("/foo/bar/toto/stuff?yadda=blah");

        Filter filter = prepare(new Filter() {
            protected void runAction(HttpServletRequest request, HttpServletResponse response, long start) throws ServletException {
                wrapException(request, new Exception("Ha!"));
            }
        });

        try {
            doFilter(filter);
            fail();
        } catch (ServletException e) {
            assertEquals("Requesting " + url, e.getMessage());
            assertEquals("Ha!", e.getRootCause().getMessage());
        }
    }
//
//    @Test
//    public void testHandlesServletPathParameters() throws IOException, ServletException, TemplateException, NoSuchMethodException {
//        uriIs("foo/controllerForTesting/methodWithParams/123/a-test-object");
//        urlIs("foo/controllerForTesting/methodWithParams/123/a-test-object");
//        String expectedContent = "rendering methodWithParams";
//        Stub returnSuccess = returnValue("yes");
//        invoke(new ControllerForTesting(), returnSuccess, expectedContent, invoker, ControllerForTesting.PARAMS_METHOD, "123, a-test-object");
//    }
//
//    @Test
//    public void testInstantiatesAndWiresController() throws Exception {
//        String expectedContent = "this is the content of the doit sucks_S file";
//        Stub returnSuccess = returnValue("sucks_S");
//        checkFilterHandlesInvokerBehaviour(returnSuccess, expectedContent);
//    }
//
//    @Test
//    public void testFindsExceptionTemplateIfControllerThrows() throws Exception {
//        String expectedContent = "this is the content of the doit NullPointerException file: foo";
//        Stub failToInvoke = throwException(new InvocationFailed(new NullPointerException("foo")));
//        checkFilterHandlesInvokerBehaviour(failToInvoke, expectedContent);
//    }
//
//    @Test
//    public void testRendersActionProperties() throws Exception {
//        String expectedContent = "doit size is 8192";
//        Stub returnSuccess = returnValue("success");
//        ControllerForTesting controller = new ControllerForTesting();
//        controller.setDoitSize(8192);
//        checkFilterHandlesInvokerBehaviour(controller, returnSuccess, expectedContent);
//    }
//
//    @Test
//    public void testUsesInvokerSpecifiedInActionMethodAnnotation() throws Exception {
//        String expectedContent = "annotated action";
//        Stub returnSuccess = returnValue("success");
//        ControllerForTesting controller = new ControllerForTesting();
//        uriIs("foo/controllerForTesting/anOtatedMethod");
//        urlIs("foo/controllerForTesting/anOtatedMethod?yadda=blah");
//        invoke(controller, returnSuccess, expectedContent, otherInvoker, ControllerForTesting.ANNOTATED_METHOD, "");
//}

    @Test
    public void testOgnlRetrievesIntValues() throws Exception {
        prepare(new Filter());
        ControllerForTesting controller = new ControllerForTesting();
        controller.setDoitSize(9979);
        Object v = Ognl.getValue("doitSize", controller);
        assertEquals(new Integer(9979), v);
    }


//    @Test
//    public void testInjectsResponseFormat() throws Exception {
//        String expectedContent = "annotated action";
//        Stub returnSuccess = returnValue("success");
//        ControllerForTesting controller = new ControllerForTesting();
//        uriIs("foo/controllerForTesting/anOtatedMethod.png");
//        urlIs("foo/controllerForTesting/anOtatedMethod.png?yadda=blah");
//        formatIs("png");
//        spring.expects(once()).method("autowire").with(eq(ControllerForTesting.class), eq(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME), eq(false)).will(returnValue(controller));
//        res.expects(once()).method("setContentType").with(eq("image/png"));
//
//        otherInvoker.expects(once()).method("invoke").with(
//                same(controller),
//                new ToStringIs("Invocation risible.core.foo.ControllerForTesting." + ControllerForTesting.ANNOTATED_METHOD.getName() + "([" + "" + "])"),
//                requestParams(params)).will(returnSuccess);
//
//        Filter filter = prepare(new Filter());
//        ExistenceDispatchingStrategy cs = new ExistenceDispatchingStrategy();
//        cs.setServletContext(new MockServletContext() {
//            @Override
//            public String getRealPath(String s) {
//                return "./" + s;
//            }
//        });
//        filter.setDispatchingStrategy(cs);
//
//        doFilter(filter);
//
//        assertEquals(expectedContent, res.content());
//    }

    private void formatIs(String format) {
        this.format = format;
    }

//    private void invoke(ControllerForTesting controller, Stub doSomething, String renderedContent, Mock targetInvoker, Method method, String methodParams) throws TemplateException, IOException, ServletException, NoSuchMethodException {
//        spring.expects(once()).method("autowire").with(eq(ControllerForTesting.class), eq(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME), eq(false)).will(returnValue(controller));
//        res.expects(once()).method("setContentType").with(eq("text/html"));
//
//        targetInvoker.expects(once()).method("invoke").with(
//                same(controller),
//                new ToStringIs("Invocation risible.core.foo.ControllerForTesting." + method.getName() + "([" + methodParams + "])"),
//                requestParams(params)).will(doSomething);
//
//        Filter filter = prepare(new Filter());
//
//        doFilter(filter);
//
//        assertEquals(renderedContent, res.content());
//    }
//
//    private Constraint requestParams(final Map params) {
//        return new Constraint() {
//            public boolean eval(Object o) {
//                if (o instanceof Map) {
//                    Map m = new HashMap((Map) o);
//                    Object req = m.remove("request");
//                    Object session = m.remove("session");
//                    Object cookies = m.remove("cookies");
//                    boolean formatOK = checkFormat((String) m.remove("responseFormat"));
//
//                    return formatOK && req instanceof HttpServletRequest && session instanceof SessionMap && cookies instanceof CookieManager && m.equals(params);
//                }
//                return false;
//            }
//
//            private boolean checkFormat(String f) {
//                return format.equals(f);
//            }
//
//            public StringBuffer describeTo(StringBuffer sb) {
//                return sb.append("A Map containing 'request', 'session' and 'cookies' keys, as well as " + params);
//            }
//        };
//    }
//
//
//    private void checkFilterHandlesInvokerBehaviour(Stub stub, String renderedContent) throws IOException, ServletException, TemplateException, NoSuchMethodException {
//        checkFilterHandlesInvokerBehaviour(new ControllerForTesting(), stub, renderedContent);
//    }
//
//    private void checkFilterHandlesInvokerBehaviour(ControllerForTesting controller, Stub doSomething, String renderedContent) throws IOException, ServletException, TemplateException, NoSuchMethodException {
//        checkFilterHandlesControllerBehaviour(controller, doSomething, renderedContent, invoker);
//    }
//
//    private void checkFilterHandlesControllerBehaviour(ControllerForTesting controller, Stub doSomething, String renderedContent, Mock targetInvoker) throws TemplateException, IOException, ServletException, NoSuchMethodException {
//        uriIs("foo/controllerForTesting/doit");
//        urlIs("foo/controllerForTesting/doit?yadda=blah");
//        invoke(controller, doSomething, renderedContent, targetInvoker, ControllerForTesting.DOIT_METHOD, "");
//    }

    private void urlIs(String url) {
        this.url = url;
        when(req.getRequestURL()).thenReturn(new StringBuffer(url));
    }

    private void doFilter(Filter filter) throws IOException, ServletException {
        filter.doFilter((ServletRequest) req, (ServletResponse) res, (FilterChain) chain);
    }

    private void expectDoFilterCall(VerificationMode numberOfTimesDoFilterIsCalled) throws IOException, ServletException {
        verify(chain, numberOfTimesDoFilterIsCalled).doFilter(req, res);
    }

    private void uriIs(String uri) {
        this.uri = uri;
        when(req.getServletPath()).thenReturn(uri);
        when(req.getRequestURI()).thenReturn(uri);
    }

//    private static class ToStringIs implements Constraint {
//        private String expected;
//
//        public ToStringIs(String expected) {
//            this.expected = expected;
//        }
//
//        public boolean eval(Object o) {
//            return o.toString().equals(expected);
//        }
//
//        public StringBuffer describeTo(StringBuffer stringBuffer) {
//            return stringBuffer.append("toString() is \"" + expected + "\"");
//        }
//    }

}
