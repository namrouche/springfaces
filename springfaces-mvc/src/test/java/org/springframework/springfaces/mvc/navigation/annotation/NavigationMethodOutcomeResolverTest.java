package org.springframework.springfaces.mvc.navigation.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.springfaces.mvc.method.support.FacesResponseCompleteReturnValueHandler;
import org.springframework.springfaces.mvc.navigation.NavigationContext;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.support.AbstractMessageConverterMethodProcessor;

/**
 * Tests for {@link NavigationMethodOutcomeResolver}.
 * 
 * @author Phillip Webb
 */
public class NavigationMethodOutcomeResolverTest {

	private NavigationMethodOutcomeResolver resolver;

	protected ServletInvocableHandlerMethod invocableNavigationMethod;

	protected InvocableHandlerMethod invocableBinderMethod;

	private Bean bean = new Bean();

	@Mock
	private ApplicationContext applicationContext;

	@Mock
	private NavigationContext context;

	@Mock
	private FacesContext facesContext;

	@Mock
	private ExternalContext externalContext;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Captor
	private ArgumentCaptor<HandlerMethodArgumentResolverComposite> argumentResolvers;

	@Captor
	private ArgumentCaptor<HandlerMethodReturnValueHandlerComposite> returnValueHandlers;

	@Captor
	private ArgumentCaptor<WebDataBinderFactory> dataBinderFactory;

	@Before
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setup() {
		MockitoAnnotations.initMocks(this);
		resolver = new NavigationMethodOutcomeResolver() {
			@Override
			protected ServletInvocableHandlerMethod createInvocableNavigationMethod(Object handler, Method method) {
				invocableNavigationMethod = spy(super.createInvocableNavigationMethod(handler, method));
				return invocableNavigationMethod;
			}

			protected InvocableHandlerMethod createInvocableBinderMethod(Object handler, Method method) {
				invocableBinderMethod = spy(super.createInvocableBinderMethod(handler, method));
				return invocableBinderMethod;
			};
		};
		given(applicationContext.getBeanNamesForType(Object.class)).willReturn(new String[] { "bean" });
		given(applicationContext.getType("bean")).willReturn((Class) Bean.class);
		given(applicationContext.getBean("bean")).willReturn(bean);
		given(context.getOutcome()).willReturn("navigate");
		given(facesContext.getExternalContext()).willReturn(externalContext);
		given(externalContext.getRequest()).willReturn(request);
		given(externalContext.getResponse()).willReturn(response);
	}

	@Test
	public void shouldCreateDefaultMessageConverters() throws Exception {
		resolver.setApplicationContext(applicationContext);
		resolver.afterPropertiesSet();
		List<HttpMessageConverter<?>> springDefaults = new RequestMappingHandlerAdapter().getMessageConverters();
		List<HttpMessageConverter<?>> actual = resolver.getMessageConverters();
		Set<Class<?>> classes = new HashSet<Class<?>>();
		for (HttpMessageConverter<?> httpMessageConverter : springDefaults) {
			classes.add(httpMessageConverter.getClass());
		}
		for (HttpMessageConverter<?> httpMessageConverter : actual) {
			classes.remove(httpMessageConverter.getClass());
		}
		assertTrue(classes.isEmpty());
	}

	@Test
	public void shouldSetCustomArgumentResolvers() throws Exception {
		HandlerMethodArgumentResolver customArgumentResolver = mock(HandlerMethodArgumentResolver.class);
		List<HandlerMethodArgumentResolver> customArgumentResolvers = Arrays.asList(customArgumentResolver);
		resolver.setApplicationContext(applicationContext);
		resolver.setCustomArgumentResolvers(customArgumentResolvers);
		resolver.afterPropertiesSet();
		resolver.resolve(facesContext, context);
		verify(invocableNavigationMethod).setHandlerMethodArgumentResolvers(argumentResolvers.capture());
		List<HandlerMethodArgumentResolver> actual = listFieldValue(argumentResolvers.getValue(), "argumentResolvers");
		assertTrue(actual.size() > 1);
		assertTrue(actual.contains(customArgumentResolver));
	}

	@Test
	public void shouldSetArgumentResolvers() throws Exception {
		HandlerMethodArgumentResolver customArgumentResolver = mock(HandlerMethodArgumentResolver.class);
		List<HandlerMethodArgumentResolver> customArgumentResolvers = Arrays.asList(customArgumentResolver);
		resolver.setApplicationContext(applicationContext);
		resolver.setArgumentResolvers(customArgumentResolvers);
		resolver.afterPropertiesSet();
		resolver.resolve(facesContext, context);
		verify(invocableNavigationMethod).setHandlerMethodArgumentResolvers(argumentResolvers.capture());
		List<HandlerMethodArgumentResolver> actual = listFieldValue(argumentResolvers.getValue(), "argumentResolvers");
		assertTrue(actual.size() == 1);
		assertTrue(actual.contains(customArgumentResolver));
	}

	@Test
	public void shouldSetInitBinderArgumentResolvers() throws Exception {
		HandlerMethodArgumentResolver customArgumentResolver = mock(HandlerMethodArgumentResolver.class);
		List<HandlerMethodArgumentResolver> customArgumentResolvers = Arrays.asList(customArgumentResolver);
		resolver.setApplicationContext(applicationContext);
		resolver.setInitBinderArgumentResolvers(customArgumentResolvers);
		resolver.afterPropertiesSet();
		resolver.resolve(facesContext, context);
		verify(invocableBinderMethod).setHandlerMethodArgumentResolvers(argumentResolvers.capture());
		List<HandlerMethodArgumentResolver> actual = listFieldValue(argumentResolvers.getValue(), "argumentResolvers");
		assertTrue(actual.size() == 1);
		assertTrue(actual.contains(customArgumentResolver));
	}

	@Test
	public void shouldSetCustomReturnValueHandlers() throws Exception {
		HandlerMethodReturnValueHandler customReturnValueHandler = mock(HandlerMethodReturnValueHandler.class);
		List<HandlerMethodReturnValueHandler> customReturnValueHandlers = Arrays.asList(customReturnValueHandler);
		resolver.setApplicationContext(applicationContext);
		resolver.setCustomReturnValueHandlers(customReturnValueHandlers);
		resolver.afterPropertiesSet();
		resolver.resolve(facesContext, context);
		verify(invocableNavigationMethod).setHandlerMethodReturnValueHandlers(returnValueHandlers.capture());
		List<HandlerMethodReturnValueHandler> actual = listFieldValue(returnValueHandlers.getValue(),
				"returnValueHandlers");
		assertTrue(actual.size() > 1);
		assertTrue(actual.contains(customReturnValueHandler));
	}

	@Test
	public void shouldSetReturnValueHandlers() throws Exception {
		HandlerMethodReturnValueHandler customReturnValueHandler = mock(HandlerMethodReturnValueHandler.class);
		given(customReturnValueHandler.supportsReturnType(any(MethodParameter.class))).willReturn(true);
		List<HandlerMethodReturnValueHandler> customReturnValueHandlers = Arrays.asList(customReturnValueHandler);
		resolver.setApplicationContext(applicationContext);
		resolver.setReturnValueHandlers(customReturnValueHandlers);
		resolver.afterPropertiesSet();
		resolver.resolve(facesContext, context);
		verify(invocableNavigationMethod).setHandlerMethodReturnValueHandlers(returnValueHandlers.capture());
		List<HandlerMethodReturnValueHandler> actual = listFieldValue(returnValueHandlers.getValue(),
				"returnValueHandlers");
		assertTrue(actual.size() == 1);
		assertTrue(actual.contains(customReturnValueHandler));
	}

	@Test
	public void shouldSetMessageConverters() throws Exception {
		StringHttpMessageConverter customMessageConverter = new StringHttpMessageConverter();
		List<HttpMessageConverter<?>> messageConverters = Arrays
				.<HttpMessageConverter<?>> asList(customMessageConverter);
		resolver.setApplicationContext(applicationContext);
		resolver.setMessageConverters(messageConverters);
		resolver.afterPropertiesSet();
		assertEquals(messageConverters, resolver.getMessageConverters());
		resolver.resolve(facesContext, context);
		verify(invocableNavigationMethod).setHandlerMethodReturnValueHandlers(returnValueHandlers.capture());
		List<HandlerMethodReturnValueHandler> returnHandlers = listFieldValue(returnValueHandlers.getValue(),
				"returnValueHandlers");
		int i = 0;
		for (HandlerMethodReturnValueHandler handler : returnHandlers) {
			if (handler instanceof FacesResponseCompleteReturnValueHandler) {
				handler = (HandlerMethodReturnValueHandler) new DirectFieldAccessor(handler)
						.getPropertyValue("handler");
			}
			if (handler instanceof AbstractMessageConverterMethodProcessor) {
				assertEquals(messageConverters, new DirectFieldAccessor(handler).getPropertyValue("messageConverters"));
				i++;
			}
		}
		assertEquals(2, i);
	}

	@Test
	public void shouldSetWebBindingInitializer() throws Exception {
		WebBindingInitializer webBindingInitializer = mock(WebBindingInitializer.class);
		resolver.setApplicationContext(applicationContext);
		resolver.setWebBindingInitializer(webBindingInitializer);
		resolver.afterPropertiesSet();
		assertSame(webBindingInitializer, resolver.getWebBindingInitializer());
		resolver.resolve(facesContext, context);
		verify(invocableNavigationMethod).setDataBinderFactory(dataBinderFactory.capture());
		NativeWebRequest webRequest = mock(NativeWebRequest.class);
		String objectName = "name";
		Object target = new Object();
		dataBinderFactory.getValue().createBinder(webRequest, target, objectName);
		verify(webBindingInitializer).initBinder(bean.binder, webRequest);
	}

	@Test
	public void shouldSetParameterNameDiscoverer() throws Exception {
		// FIXME
	}

	@Test
	public void shouldDetectNavigationMethodsOnController() throws Exception {
		// FIXME
	}

	@Test
	public void shouldDetectNavigationMethodsOnNavigationController() throws Exception {
		// FIXME
	}

	@Test
	public void shouldNotDetectNavigationMethodsOnComponent() throws Exception {
		// FIXME
	}

	@Test
	public void shouldInitDefaultArgumentResolvers() throws Exception {
		// FIXME
	}

	@Test
	public void shouldInitDefaultBinderArgumentResolvers() throws Exception {
		// FIXME
	}

	@Test
	public void shouldInitDefaultReturnValueHandlers() throws Exception {
		// FIXME
	}

	@Test
	public void shouldSupportCanResolve() throws Exception {
		// FIXME
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> listFieldValue(Object object, String field) {
		return (List<T>) new DirectFieldAccessor(object).getPropertyValue(field);
	}

	@NavigationController
	public static class Bean {
		public WebDataBinder binder;

		@NavigationMapping
		public void onNavigate() {
		}

		@InitBinder
		public void initBinder(WebDataBinder binder) {
			this.binder = binder;
		}
	}
}
