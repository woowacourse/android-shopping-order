package woowacourse.shopping.shoppingCartRecommend

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.common.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R
import woowacourse.shopping.data.product.repository.DefaultProductsRepository
import woowacourse.shopping.fixture.RECENT_PRODUCTS
import woowacourse.shopping.fixture.allRequests
import woowacourse.shopping.matcher.RecyclerViewMatcher.Companion.withRecyclerView
import woowacourse.shopping.matcher.isDisplayed
import woowacourse.shopping.matcher.matchSize
import woowacourse.shopping.matcher.matchText
import woowacourse.shopping.matcher.performClick
import woowacourse.shopping.matcher.scrollToPosition
import woowacourse.shopping.rule.MockServerRule
import woowacourse.shopping.view.shoppingCartRecommend.ShoppingCartRecommendActivity

@Suppress("FunctionName")
@RunWith(AndroidJUnit4::class)
class ShoppingCartRecommendTest {
    private var scenario = ActivityScenario.launch(ShoppingCartRecommendActivity::class.java)

    @Rule
    @JvmField
    val mockServerRule = MockServerRule(scenario)

    @Before
    fun setUp() {
        scenario.onActivity {
            runBlocking {
                val repository = DefaultProductsRepository.get()
                RECENT_PRODUCTS.forEach {
                    repository.updateRecentWatchingProduct(it)
                }
            }
            it.viewModelStore.clear()
        }
        scenario.recreate()
        Thread.sleep(100)
    }

    @Test
    fun 화면_초기_진입_시_주요_UI_요소들이_표시된다() {
        onView(withId(R.id.shoppingCartBackButton)).isDisplayed()
        onView(withId(R.id.shoppingCartRecommendTitle))
            .matchText(
                "이런 상품은 어떠세요?",
            )
        onView(withId(R.id.shoppingCartRecommendDescription))
            .matchText("* 최근 본 상품 기반으로 좋아하실 것 같은 상품들을 추천해드려요.")
        onView(withId(R.id.shoppingCartRecommendProducts)).isDisplayed()
        onView(withId(R.id.shoppingCartRecommendOrderTotalPrice)).isDisplayed()
        onView(withId(R.id.shoppingCartRecommendOrderButton)).isDisplayed()
    }

    @Test
    fun 추천_상품은_마지막으로_본_상품의_카테고리에_속하는_최근_본_상품이다() {
        // given - 최근 본 상품, 패션잡화,
        // 플라망고는 식료품
        val mustNotHaveProductsName = "플라망고"

        Thread.sleep(100)

        // when - then
        onView(
            withRecyclerView(R.id.shoppingCartRecommendProducts)
                .atPositionOnView(0, R.id.productName),
        ).matchText("에어포스31")

        // 플라망고는 식료품
        for (i in 0..9) {
            onView(withId(R.id.shoppingCartRecommendProducts))
                .perform(scrollToPosition(i))
            onView(
                withRecyclerView(R.id.shoppingCartRecommendProducts)
                    .atPositionOnView(i, R.id.productName),
            ).check(matches(not(withText(mustNotHaveProductsName))))
        }
    }

    @Test
    fun 장바구니에_있는_상품은_추천_상품에_없다() {
        // given - 에어포스2는 장바구니에 있습니다
        val mustNotHaveProductsName = "에어포스2"

        // when - then
        for (i in 0..9) {
            onView(withId(R.id.shoppingCartRecommendProducts))
                .perform(scrollToPosition(i))
            onView(
                withRecyclerView(R.id.shoppingCartRecommendProducts)
                    .atPositionOnView(i, R.id.productName),
            ).check(matches(not(withText(mustNotHaveProductsName))))
        }
    }

    @Test
    fun 추천_상품은_최대_10개까지_표시된다() {
        // given - 추천 상품은 10개가 넘습니다
        scenario.onActivity {
            runBlocking {
                val repository = DefaultProductsRepository.get()
                RECENT_PRODUCTS.forEach {
                    repository.updateRecentWatchingProduct(it)
                }
            }
        }
        scenario.close()
        scenario = ActivityScenario.launch(ShoppingCartRecommendActivity::class.java)

        // when - then
        onView(withId(R.id.shoppingCartRecommendProducts))
            .check(matchSize(10))
    }

    @Test
    fun 추천_상품을_장바구니에_담을_수_있다() {
        // given - when

        Thread.setDefaultUncaughtExceptionHandler { t, e -> }
        onView(
            withRecyclerView(R.id.shoppingCartRecommendProducts)
                .atPositionOnView(0, R.id.productPlusQuantityButtonDefault),
        )
            .performClick()

        // then
        val recorded = mockServerRule.mockShoppingCartServer.allRequests()

        val result =
            recorded.any {
                val body = it.body.readUtf8()
                it.method == "POST" && it.path == "/cart-items" &&
                    body.contains("\"quantity\":1") &&
                    body.contains("\"productId\":36")
            }

        assertThat(result).isTrue()
        Thread.setDefaultUncaughtExceptionHandler { t, e -> throw e }
    }
}
