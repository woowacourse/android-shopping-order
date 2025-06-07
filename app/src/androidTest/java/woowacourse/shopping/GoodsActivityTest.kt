package woowacourse.shopping

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.feature.cart.CartActivity
import woowacourse.shopping.feature.goods.GoodsActivity
import woowacourse.shopping.feature.goods.adapter.GoodsViewHolder
import woowacourse.shopping.feature.goodsdetails.GoodsDetailsActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class GoodsActivityTest {
    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun 상품리스트가_보이고_스크롤된다() {
        ActivityScenario.launch(GoodsActivity::class.java)

        Thread.sleep(1000)

        onView(withId(R.id.rv_goods))
            .check(matches(isDisplayed()))
            .perform(RecyclerViewActions.scrollToPosition<GoodsViewHolder>(5))
    }

    @Test
    fun 상품을_클릭하면_상세페이지로_이동한다() {
        ActivityScenario.launch(GoodsActivity::class.java)

        Thread.sleep(2000)

        onView(withId(R.id.rv_goods))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<GoodsViewHolder>(
                    0,
                    click(),
                ),
            )

        Intents.intended(hasComponent(GoodsDetailsActivity::class.java.name))
    }

    @Test
    fun 장바구니아이콘_클릭시_CartActivity로_이동한다() {
        ActivityScenario.launch(GoodsActivity::class.java)

        onView(withId(R.id.nav_cart)).perform(click())

        Intents.intended(hasComponent(CartActivity::class.java.name))
    }

    private fun clickView(viewId: Int): ViewAction =
        object : ViewAction {
            override fun getDescription() = "지정된 ID의 뷰를 클릭합니다."

            override fun getConstraints() = null

            override fun perform(
                uiController: androidx.test.espresso.UiController?,
                view: View?,
            ) {
                val view = view?.findViewById<View>(viewId)
                view?.performClick()
            }
        }
}
