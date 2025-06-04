@file:Suppress("ktlint")

package woowacourse.shopping.productDetail

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import io.kotest.common.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.R
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.fixture.fakeContext
import woowacourse.shopping.matcher.isDisplayed
import woowacourse.shopping.matcher.matchText
import woowacourse.shopping.matcher.performClick
import woowacourse.shopping.rule.MockServerRule
import woowacourse.shopping.view.productDetail.ProductDetailActivity

class ProductDetailActivityTest {
    private val intent = ProductDetailActivity.newIntent(fakeContext, 1)
    private var scenario = ActivityScenario.launch<ProductDetailActivity>(intent)

    @Rule
    @JvmField
    val mockServerRule = MockServerRule(scenario)

    @Before
    fun setUp() {
        scenario.onActivity {
            it.viewModelStore.clear()
        }
        scenario.recreate()
    }

    @Test
    fun 상품의_이름_이미지와_가격이_표시된다() {
        onView(withId(R.id.productDetailRoot)).isDisplayed()
        onView(withId(R.id.productDetailName)).matchText("에어포스1에어포스1에어포스1에어포스1")
        onView(withId(R.id.productDetailPrice)).matchText("100,000원")
    }

    @Test
    fun 장바구니_담기를_클릭하면_선택한_수량만큼_장바구니에_상품이_담긴다() {
        // given
        onView(withId(R.id.productPlusQuantityButton)).performClick()
        onView(withId(R.id.productPlusQuantityButton)).performClick()

        // when
        onView(withId(R.id.productDetailShoppingCartButton)).performClick()

        // then
        val recorded =
            buildList {
                repeat(mockServerRule.mockShoppingCartServer.requestCount) {
                    add(mockServerRule.mockShoppingCartServer.takeRequest())
                }
            }

        val result =
            recorded.any {
                it.method == "PATCH" && it.path == "/cart-items/0" &&
                        it.body.readUtf8()
                            .contains("{\"quantity\" : 3}")
            }
        assertThat(result).isTrue()
    }

    @Test
    fun 가장_최근_본_상품이_있으면_최근_본_상품의_정보가_출력된다() {
        // given
        scenario.onActivity { activity ->
            runBlocking {
                DefaultProductsRepository.get().updateRecentWatchingProduct(
                    Product(
                        id = 2,
                        name = "에어포스2",
                        price = 100000,
                        imageUrl = "https://kream-phinf.pstatic.net/MjAyNTA1MTNfMjI5/MDAxNzQ3MTA4MjUzOTg4.106G0-WfVU8g8ziNKgKJjc1_UXvF-2IatsA-Cz5mG1og.etXRFVPYqcs5J9HAfXpaHFPFHorGnZU4Nl7k4368rfog.PNG/a_090d2310040b4f9ca922f2498ae8ae3a.png?type=l",
                        category = "패션잡화",
                    ),
                )
            }
        }
        scenario.close()
        scenario = ActivityScenario.launch<ProductDetailActivity>(intent)

        // when - then
        onView(withId(R.id.productDetailRecentWatchingName))
            .isDisplayed()
            .matchText("에어포스2")
    }

    @Test
    fun 최근에_본_상품에_들어가면_최근_본_상품의_정보가_출력되지_않는다() {
        scenario.onActivity { activity ->
            runBlocking {
                DefaultProductsRepository.get().updateRecentWatchingProduct(
                    Product(
                        id = 2,
                        name = "에어포스2",
                        price = 100000,
                        imageUrl = "https://kream-phinf.pstatic.net/MjAyNTA1MTNfMjI5/MDAxNzQ3MTA4MjUzOTg4.106G0-WfVU8g8ziNKgKJjc1_UXvF-2IatsA-Cz5mG1og.etXRFVPYqcs5J9HAfXpaHFPFHorGnZU4Nl7k4368rfog.PNG/a_090d2310040b4f9ca922f2498ae8ae3a.png?type=l",
                        category = "패션잡화",
                    ),
                )
            }
        }
        val intent2 = ProductDetailActivity.newIntent(fakeContext, 2)
        scenario = ActivityScenario.launch(intent2)

        onView(withId(R.id.productDetailRecentWatchingName))
            .check(matches(not(ViewMatchers.isDisplayed())))
    }
}
