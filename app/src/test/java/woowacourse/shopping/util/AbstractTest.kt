package woowacourse.shopping.util

import io.mockk.checkUnnecessaryStub
import io.mockk.junit5.MockKExtension
import org.junit.After

@MockKExtension.CheckUnnecessaryStub
abstract class AbstractTest {

    @After
    fun tearDown() {
        checkUnnecessaryStub()
    }
}
