package woowacourse.shopping.order

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R
import woowacourse.shopping.fixture.PRODUCT_TO_ORDER
import woowacourse.shopping.fixture.fakeContext
import woowacourse.shopping.matcher.RecyclerViewMatcher.Companion.withRecyclerView
import woowacourse.shopping.matcher.matchSize
import woowacourse.shopping.matcher.matchText
import woowacourse.shopping.matcher.performClick
import woowacourse.shopping.rule.MockServerRule
import woowacourse.shopping.view.order.OrderActivity

@Suppress("FunctionName")
@RunWith(AndroidJUnit4::class)
class OrderActivityTest {
    private val intent =
        OrderActivity.newIntent(
            fakeContext,
            PRODUCT_TO_ORDER,
        )
    private val scenario = ActivityScenario.launch<OrderActivity>(intent)

    @Rule
    @JvmField
    val mockServerRule = MockServerRule(scenario)

    @Before
    fun setUp() {
        scenario.onActivity {
            it.viewModelStore.clear()
        }
        scenario.recreate()
        Thread.sleep(100)
    }

    @Test
    fun 적용_가능한_쿠폰이_나타난다() {
        // given
        // 구매할 상품 가격 : 100,000원, 수량 : 2

        // when - then
        onView(
            withRecyclerView(R.id.coupons)
                .atPositionOnView(0, R.id.itemCouponTitle),
        ).matchText("5,000원 할인 쿠폰")

        onView(
            withRecyclerView(R.id.coupons)
                .atPositionOnView(1, R.id.itemCouponTitle),
        ).matchText("5만원 이상 구매 시 무료 배송 쿠폰")

        onView(withId(R.id.coupons))
            .check(matchSize(2))
    }

    @Test
    fun 쿠폰은_하나만_선택할_수_있다() {
        // given
        // 구매할 상품 가격 : 100,000원, 수량 : 2
        onView(
            withRecyclerView(R.id.coupons)
                .atPositionOnView(0, R.id.itemCouponTitle),
        ).performClick()

        // when
        onView(
            withRecyclerView(R.id.coupons)
                .atPositionOnView(1, R.id.itemCouponTitle),
        ).performClick()

        // then
        onView(
            withRecyclerView(R.id.coupons)
                .atPositionOnView(0, R.id.couponCheckbox),
        ).check(matches(not(isChecked())))
    }

    @Test
    fun 가격과_할인_배송비_정보가_표시된다() {
        // given
        // 구매할 상품 가격 : 100,000원, 수량 : 2 배송비 3000원
        // 5000원 할인 클릭
        onView(
            withRecyclerView(R.id.coupons)
                .atPositionOnView(0, R.id.itemCouponTitle),
        ).performClick()

        // when - then
        onView(withId(R.id.totalPrice))
            .matchText("200,000원")

        onView(withId(R.id.couponDiscount))
            .matchText("-5,000원")

        onView(withId(R.id.shippingFee))
            .matchText("3,000원")

        onView(withId(R.id.finalPrice))
            .matchText("198,000원")
    }
}
