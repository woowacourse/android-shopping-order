package woowacourse.shopping.study

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.OverrideMockKs
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.presentation.CoroutinesTestExtension


@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class, MockKExtension::class)
class OverrideMockKs {

    val name = "Hwang Tae Jun"
    val age = 26
    val nickname = "Hevton"

    @InjectMockKs(overrideValues = true)
    private lateinit var person1: Person

    @OverrideMockKs
    private lateinit var person2: Person


    @Test
    fun `InjectedMockKs 기능`() {
        assertSoftly {
            person1.name shouldBe "Hwang Tae Jun"
            person1.age shouldBe  26
            person1.nickname shouldBe "Hevton" // val은 overrideValues = true 해도 주입 안됨
        }
    }

    @Test
    fun `OverrideMockKs 기능`() {
        assertSoftly {
            person2.name shouldBe  "Hwang Tae Jun"
            person2.age shouldBe  26
            person2.nickname shouldBe "Hevton" // val override 까지 하기 위해선 InjectedMockKs가 아닌 OverrideMockKs 사용
        }
    }
}


class Person(val name: String, val age: Int) {

    val nickname = "PangTae"
}