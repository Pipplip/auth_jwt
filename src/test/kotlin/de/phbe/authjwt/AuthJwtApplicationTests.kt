package de.phbe.authjwt

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AuthJwtApplicationTests: FunSpec( {
    test("context loads") {
        // nothing to do, wenn Context startet -> Test grün
    }
})
