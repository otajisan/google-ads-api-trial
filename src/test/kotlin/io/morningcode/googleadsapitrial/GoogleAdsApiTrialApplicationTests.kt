package io.morningcode.googleadsapitrial

import io.morningcode.googleadsapitrial.application.GoogleAdsApiController
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GoogleAdsApiTrialApplicationTests {

	@Autowired
	private lateinit var controller: GoogleAdsApiController

	@Test
	fun contextLoads() {
		assertThat(controller).isNotNull
	}

}
