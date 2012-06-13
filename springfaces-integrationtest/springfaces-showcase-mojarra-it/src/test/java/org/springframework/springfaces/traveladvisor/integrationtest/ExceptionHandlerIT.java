/*
 * Copyright 2010-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.springfaces.traveladvisor.integrationtest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.springfaces.integrationtest.selenium.rule.Pages;
import org.springframework.springfaces.traveladvisor.integrationtest.page.exceptionhandler.HandledElCallPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.exceptionhandler.HandledNavigationMappingPage;
import org.springframework.springfaces.traveladvisor.integrationtest.page.exceptionhandler.OutcomePage;
import org.springframework.springfaces.traveladvisor.integrationtest.rule.ShowcasePages;

/**
 * Integration tests for exception handlers.
 * 
 * @author Phillip Webb
 */
public class ExceptionHandlerIT {

	@Rule
	public Pages pages = new ShowcasePages();

	@Test
	public void shouldHandleElException() throws Exception {
		HandledElCallPage page = this.pages.get(HandledElCallPage.class);
		OutcomePage outcome = page.click();
		assertThat(outcome.getBodyText(), is("Exception has been handled"));
	}

	@Test
	public void shouldHandleNavigationMappingException() throws Exception {
		HandledNavigationMappingPage page = this.pages.get(HandledNavigationMappingPage.class);
		OutcomePage outcome = page.click();
		assertThat(outcome.getBodyText(), is("Exception has been handled"));
	}

}
