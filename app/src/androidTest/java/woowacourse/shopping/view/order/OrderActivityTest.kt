package woowacourse.shopping.view.order

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R
import woowacourse.shopping.util.FakeShoppingApplication

@RunWith(AndroidJUnit4::class)
class OrderActivityTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val application = context.applicationContext as FakeShoppingApplication
    private lateinit var scenario: ActivityScenario<OrderActivity>

    @Before
    fun setUp() =
        runTest {
            val entireCartItems = application.cartRepository.getEntireCartItems().getOrThrow()
            scenario =
                ActivityScenario.launch(
                    OrderActivity.createIntent(
                        ApplicationProvider.getApplicationContext(),
                        entireCartItems,
                    ),
                )
        }

    @Test
    fun `금액_관련_정보를_보여준다`() =
        runTest {
            onView(withId(R.id.item_order_price))
                .check(matches(hasDescendant(withText("385,000원"))))

            onView(withId(R.id.item_discount_price))
                .check(matches(hasDescendant(withText("-0원"))))

            onView(withId(R.id.item_shipping_price))
                .check(matches(hasDescendant(withText("3,000원"))))

            onView(withId(R.id.item_total_price))
                .check(matches(hasDescendant(withText("388,000원"))))
        }

    @Test
    fun `사용할_수_있는_쿠폰들을_보여준다`() {
        onView(withId(R.id.rv_coupons)).check(matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.rv_coupons))
            .check(
                matches(
                    Matchers.allOf(
                        hasDescendant(withText("5,000원 할인 쿠폰")),
                        hasDescendant(withText("만료일: 2024년 11월 30일")),
                        hasDescendant(withText("최소 주문 금액: 100,000원")),
                        hasDescendant(withText("5만원 이상 구매 시 무료 배송 쿠폰")),
                        hasDescendant(withText("만료일: 2024년 08월 31일")),
                        hasDescendant(withText("최소 주문 금액: 50,000원")),
                    ),
                ),
            )
    }
}
