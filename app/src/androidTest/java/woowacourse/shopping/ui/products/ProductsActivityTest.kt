package woowacourse.shopping.ui.products

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R

@RunWith(AndroidJUnit4::class)
class ProductsActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(ProductsActivity::class.java)

    @Test
    fun `상품_목록이_보인다`() {
        onView(withId(R.id.rv_products))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}
