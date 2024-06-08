package woowacourse.shopping.ui.coupon

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R

@RunWith(AndroidJUnit4::class)
class CouponActivityTest {
    private val intent =
        Intent(ApplicationProvider.getApplicationContext(), CouponActivity::class.java)
            .putExtra("selected_cart_item_ids", intArrayOf())

    @get:Rule
    val activityRule = ActivityScenarioRule<CouponActivity>(intent)

    @Test
    fun `적용할_수_있는_쿠폰이_없는_경우_쿠폰_목록이_보이지_않는다`() {
        onView(withId(R.id.rv_coupon))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}
