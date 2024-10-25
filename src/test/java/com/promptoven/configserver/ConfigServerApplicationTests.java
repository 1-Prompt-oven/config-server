package com.promptoven.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(
	properties = {
		"spring.cloud.config.server.git.uri=https://github.com/Prompt-oven/config-server",
		"spring.cloud.config.server.git.searchPaths=configs",
		"spring.cloud.config.server.git.default-label=main",
		"spring.cloud.config.server.git.basedir=target/config-repo"
	},
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
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

		System.out.println("now testing /discovery/default");
		System.out.println("expectedJson: " + expectedJson);
		mockMvc.perform(get("/discovery/main"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andDo(result -> System.out.println(result.getResponse().getContentAsString()))
			.andExpect(content().json(expectedJson));

	}
	    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testConfigServerExposeConfigEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity("/discovery/main", String.class);
        System.out.println("Response status: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("propertySources");
        // Test actuator endpoint
        ResponseEntity<String> actuatorResponse = restTemplate.getForEntity("/actuator/env", String.class);
        System.out.println("Actuator response status: " + actuatorResponse.getStatusCode());
        System.out.println("Actuator response body: " + actuatorResponse.getBody());
    }
}