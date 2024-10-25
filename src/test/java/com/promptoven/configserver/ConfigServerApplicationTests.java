package com.promptoven.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class ConfigServerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldProvideConfigFile() throws Exception {
		String expectedJson = """
			{
			  "spring": {
			    "application": {
			      "name": "discovery"
			    },
			    "config": {
			      "import": "optional:configserver:http://localhost:8888"
			    },
			    "cloud": {
			      "config": {
			        "uri": "http://localhost:8888",
			        "fail-fast": true
			      }
			    }
			  },
			  "server": {
			    "port": 8761
			  },
			  "test": {
			    "value": "test"
			  }
			}""";


		mockMvc.perform(get("/discovery/default"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(content().json(expectedJson)); // Adjust the expected JSON content as needed
	}
}