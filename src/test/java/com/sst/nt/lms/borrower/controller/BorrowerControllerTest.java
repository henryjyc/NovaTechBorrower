package com.sst.nt.lms.borrower.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


/**
 * Tests of the 'borrower controller'.
 *
 * @author Salem Ozaki
 *
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration()
@AutoConfigureMockMvc
@SpringBootTest
//@ContextConfiguration(classes = {TestWebConfig.class, TestBackEndConfiguration.class})
//@TestInstance(Lifecycle.PER_CLASS)
public class BorrowerControllerTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@DisplayName("Get a borrower that exists")
	@Disabled("Requires databse setup that isn't provided here")
	@Test
	public void testGetBorrower() throws Exception {
		mockMvc.perform(get("/borrowers/1")).andExpect(status().isOk());
	}
}
