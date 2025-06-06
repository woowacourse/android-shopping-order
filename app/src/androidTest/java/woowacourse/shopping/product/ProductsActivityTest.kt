package woowacourse.shopping.product

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R
import woowacourse.shopping.fixture.allRequests
import woowacourse.shopping.fixture.page1
import woowacourse.shopping.matcher.RecyclerViewMatcher.Companion.withRecyclerView
import woowacourse.shopping.matcher.isDisplayed
import woowacourse.shopping.matcher.isEllipsized
import woowacourse.shopping.matcher.matchSize
import woowacourse.shopping.matcher.matchText
import woowacourse.shopping.matcher.performClick
import woowacourse.shopping.matcher.scrollToPosition
import woowacourse.shopping.matcher.sizeGreaterThan
import woowacourse.shopping.rule.MockServerRule
import woowacourse.shopping.view.product.ProductsActivity
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@Suppress("FunctionName")
@RunWith(AndroidJUnit4::class)
class ProductsActivityTest {
    private val mainActivityScenario = ActivityScenario.launch(ProductsActivity::class.java)

    @Rule
    @JvmField
    val mockServerRule = MockServerRule(mainActivityScenario)

    @Before
    fun setUp() {
        mainActivityScenario.onActivity {
            it.viewModelStore.clear()
        }
        mainActivityScenario.recreate()
        Thread.sleep(100)
    }

    @Test
    fun 상품의_목록이_표시된다() {
        onView(
            withRecyclerView(R.id.products).atPositionOnView(
                1,
                R.id.productName,
            ),
        ).matchText("에어포스1에어포스1에어포스1에어포스1")
        onView(
            withRecyclerView(R.id.products).atPositionOnView(
                1,
                R.id.productPrice,
            ),
        ).matchText("100,000원")
        onView(
            withRecyclerView(R.id.products).atPositionOnView(
                1,
                R.id.productImage,
            ),
        ).isDisplayed()
    }

    @Test
    fun 상품의_목록은_20개_단위로_표시된다() {
        // 최근 본 상품, 더보기 버튼 각각 +1 해서 총 22
        onView(withId(R.id.products)).check(matchSize(22))
    }

    @Test
    fun 더보기_버튼을_눌러서_상품을_추가_로드할_수_있다() {
        onView(withId(R.id.products)).perform(scrollToPosition(21))
        Thread.sleep(1000)
        onView(withId(R.id.productsMoreButton)).performClick()
        onView(withId(R.id.products)).check(sizeGreaterThan(20))
    }

    @Test
    fun 상품의_이름이_너무_길_경우_말줄임표로_표시된다() {
        onView(
            withRecyclerView(R.id.products).atPositionOnView(
                1,
                R.id.productName,
            ),
        ).check(isEllipsized())
    }

    @Test
    fun 수량을_변경하면_현재_수량_정보가_장바구니에_반영된다() {
        // given
        // 기존 수량 : 1

        // when
        onView(
            withRecyclerView(R.id.products).atPositionOnView(
                1,
                R.id.productPlusQuantityButton,
            ),
        )
            .performClick()

        // then
        val requests = mockServerRule.mockShoppingCartServer.allRequests()

        assertThat(
            requests.any { recorded -> recorded.path?.contains("/cart-items/1") == true },
        ).isTrue()

        assertThat(
            requests.any { recorded -> recorded.method == "PATCH" },
        )

        assertThat(
            requests.any { recorded ->
                recorded.body.readUtf8().contains(
                    """
                    {
                        "quantity": 2
                    }
                    """.trimIndent(),
                )
            },
        )
    }

    @Test
    fun 상품_목록에서_플러스_버튼_클릭_시_뱃지_카운트가_증가한다() {
        onView(
            withRecyclerView(R.id.products).atPositionOnView(
                1,
                R.id.productPlusQuantityButton,
            ),
        )
            .performClick()

        Thread.sleep(500)

        onView(withId(R.id.productsShoppingCartQuantity)).isDisplayed()
        onView(withId(R.id.productsShoppingCartQuantity)).matchText("3")
    }

    @Test
    fun 상품_목록에서_수량을_변경하면_상품을_다시_로딩한다() {
        // given
        // 1번째 상품의 수량 : 1

        // when
        onView(
            withRecyclerView(R.id.products).atPositionOnView(
                1,
                R.id.productPlusQuantityButton,
            ),
        )
            .performClick()

        // then
        val requests = mockServerRule.mockProductServer.allRequests()

        assertThat(
            requests.any { recorded -> recorded.path?.contains("/products") == true },
        ).isTrue()

        assertThat(
            requests.any { recorded -> recorded.method == "GET" },
        )
    }

    @Test
    fun 상품_목록에서_수량이_0일_경우_수량_선택_버튼이_아닌_플러스_버튼만_제공된다() {
        // given
        // 6번째 상품의 수량은 0

        onView(
            withRecyclerView(R.id.products).atPositionOnView(
                6,
                R.id.productPlusQuantityButtonDefault,
            ),
        )
            .isDisplayed()
    }

    @Test
    fun 최근_본_상품의_목록이_나타난다() {
        // given
        onView(
            withRecyclerView(R.id.products).atPositionOnView(
                1,
                R.id.productImage,
            ),
        )
            .performClick()

        // when
        onView(withId(R.id.detailClose))
            .performClick()

        onView(
            withRecyclerView(R.id.productRecentWatching).atPositionOnView(
                0,
                R.id.recentWatchingImage,
            ),
        )
            .isDisplayed()
    }

    @Test
    fun 최근_본_상품_목록이_10개가_넘어가면_맨_나중에_있는_아이템이_삭제된다() {
        // given - when
        for (i in 1..11) {
            onView(withId(R.id.products))
                .perform(scrollToPosition(i))

            onView(
                withRecyclerView(R.id.products)
                    .atPositionOnView(i, R.id.productImage),
            )
                .performClick()
            Thread.sleep(100)
            onView(withId(R.id.detailClose))
                .performClick()
        }

        onView(withId(R.id.products))
            .perform(swipeDown())

        onView(withId(R.id.productRecentWatching))
            .perform(scrollToPosition(9))
            .check(matchSize(10))
    }

    @Test
    fun 최근_본_상품에_있는_상품을_클릭하면_맨_앞으로_아이템이_이동한다() {
        // given
        for (i in 1..5) {
            onView(withId(R.id.products))
                .perform(scrollToPosition(i))

            onView(
                withRecyclerView(R.id.products)
                    .atPositionOnView(i, R.id.productImage),
            )
                .performClick()
            Thread.sleep(200)
            onView(withId(R.id.detailClose))
                .performClick()
        }

        // when
        onView(
            withRecyclerView(R.id.products).atPositionOnView(
                1,
                R.id.productImage,
            ),
        )
            .performClick()
        Thread.sleep(100)

        onView(withId(R.id.detailClose))
            .performClick()

        // then
        onView(withId(R.id.products))
            .perform(swipeDown())

        onView(
            withRecyclerView(R.id.productRecentWatching).atPositionOnView(
                0,
                R.id.recentWatchingName,
            ),
        )
            .isDisplayed()
            .matchText("에어포스1에어포스1에어포스1에어포스1")
    }

    @Test
    fun 상품_로딩_전_스켈레톤_UI_를_보여준다() {
        mainActivityScenario.onActivity {
            thread {
                mockServerRule.mockProductServer.dispatcher =
                    object : Dispatcher() {
                        override fun dispatch(request: RecordedRequest): MockResponse {
                            return MockResponse()
                                .setResponseCode(200)
                                .setBody(page1)
                                .setBodyDelay(2000, TimeUnit.MILLISECONDS)
                        }
                    }
                it.viewModelStore.clear()
            }.join()
        }
        mainActivityScenario.recreate()
        onView(withId(R.id.productsSkeletonLayout)).isDisplayed()
    }
}
