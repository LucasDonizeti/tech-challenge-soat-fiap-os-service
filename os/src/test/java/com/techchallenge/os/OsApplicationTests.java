package com.techchallenge.os;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Requires full Spring context with database configuration")
class OsApplicationTests {

	@Test
	void contextLoads() {
	}

}
