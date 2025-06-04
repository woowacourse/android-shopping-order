package woowacourse.shopping.shoppingCart

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.R
import woowacourse.shopping.fixture.fakeContext
import woowacourse.shopping.matcher.RecyclerViewMatcher.Companion.withRecyclerView
import woowacourse.shopping.matcher.isDisplayed
import woowacourse.shopping.matcher.matchText
import woowacourse.shopping.matcher.performClick
import woowacourse.shopping.matcher.scrollToPosition
import woowacourse.shopping.matcher.sizeGreaterThan
import woowacourse.shopping.rule.MockServerRule
import woowacourse.shopping.view.shoppingCart.ShoppingCartActivity

@Suppress("FunctionName")
class ShoppingCartActivityTest {
    private val intent = ShoppingCartActivity.newIntent(fakeContext)
    private var scenario = ActivityScenario.launch<ShoppingCartActivity>(intent)

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
    fun 장바구니에_담긴_상품의_목록이_표시된다() {
        onView(withId(R.id.shoppingCartProducts))
            .isDisplayed()
        onView(
            withRecyclerView(R.id.shoppingCartProducts)
                .atPositionOnView(0, R.id.shoppingCartProductName),
        ).matchText("에어포스2")
        onView(
            withRecyclerView(R.id.shoppingCartProducts)
                .atPositionOnView(0, R.id.shoppingCartPrice),
        ).matchText("100,000원")
    }

    @Test
    fun 장바구니에서_삭제_버튼을_누르면_서버로_요청을_보낸다() {
        onView(
            withRecyclerView(R.id.shoppingCartProducts)
                .atPositionOnView(0, R.id.shoppingCartProductDeleteButton),
        ).performClick()

        // then
        val recorded =
            buildList {
                repeat(mockServerRule.mockShoppingCartServer.requestCount) {
                    add(mockServerRule.mockShoppingCartServer.takeRequest())
                }
            }

        val result =
            recorded.any {
                it.method == "DELETE" && it.path == "/cart-items/1"
            }

        assertThat(result).isTrue()
    }

    @Test
    fun 장바구니에서_수량을_조절하면_서버에_요청을_보낸다() {
        // when
        onView(
            withRecyclerView(R.id.shoppingCartProducts)
                .atPositionOnView(0, R.id.productPlusQuantityButton),
        ).performClick()

        // then
        val recorded =
            buildList {
                repeat(mockServerRule.mockShoppingCartServer.requestCount) {
                    add(mockServerRule.mockShoppingCartServer.takeRequest())
                }
            }

        recorded.any {
            it.method == "PATCH" && it.path == "/cart-items/1" &&
                it.body.readUtf8()
                    .contains("{\"quantity\" : 2}")
        }
    }

    @Test
    fun 스크롤을_하면_다섯개_단위로_무한스크롤_된다() {
        // given - when
        onView(withId(R.id.shoppingCartProducts))
            .perform(scrollToPosition(4))

        // then
        onView(withId(R.id.shoppingCartProducts))
            .check(sizeGreaterThan(5))
    }

    @Test
    fun 상품의_체크박스가_선택된_숫자만큼_주문할_수량과_가격정보가_표시된다() {
        // when
        onView(
            withRecyclerView(R.id.shoppingCartProducts)
                .atPositionOnView(0, R.id.shoppingCartProductCheckBox),
        ).performClick()

        onView(
            withRecyclerView(R.id.shoppingCartProducts)
                .atPositionOnView(1, R.id.shoppingCartProductCheckBox),
        ).performClick()

        // then
        onView(withId(R.id.shoppingCartTotalPrice))
            .matchText("300,000원")

        onView(withId(R.id.shoppingCartOrderButton))
            .matchText("주문하기(2)")
    }

    @Test
    fun 상품의_전체_체크박스를_누르면_모든_상품이_선택된다() {
        // given
        // 전체 쇼핑카드 데이터는 7개입니다

        // when
        onView(withId(R.id.shoppingCartProductAllSelectCheckBox))
            .performClick()

        for (i in 0..6) {
            onView(
                withRecyclerView(R.id.shoppingCartProducts)
                    .atPositionOnView(i, R.id.shoppingCartProductCheckBox),
            ).check(matches(isChecked()))
        }
    }

    @Test
    fun 상품을_선택후_주문하기_버튼을_누르면_상품추천_화면오로_이동한다() {
        // given
        onView(
            withRecyclerView(R.id.shoppingCartProducts)
                .atPositionOnView(0, R.id.shoppingCartProductCheckBox),
        ).performClick()

        // when
        onView(withId(R.id.shoppingCartOrderButton))
            .performClick()

        // then
        onView(withId(R.id.shoppingCartRecommendRoot))
            .isDisplayed()
    }
}
