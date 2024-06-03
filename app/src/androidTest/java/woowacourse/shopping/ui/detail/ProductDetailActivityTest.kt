package woowacourse.shopping.ui.detail

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R

@RunWith(AndroidJUnit4::class)
class ProductDetailActivityTest {
    private val intent =
        Intent(ApplicationProvider.getApplicationContext(), ProductDetailActivity::class.java)
            .putExtra("product_id_key", 2)
            .putExtra("last_seen_product_visible", false)

    @get:Rule
    val activityRule = ActivityScenarioRule<ProductDetailActivity>(intent)

    @Test
    fun `상품_제목이_보인다`() {
        onView(withId(R.id.tv_product_detail_title))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun `상품_가격이_보인다`() {
        onView(withId(R.id.tv_product_detail_price))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun `상품_개수가_1개_이상인_경우_장바구니_담기_버튼이_활성화_된다`() {
        onView(withId(R.id.btn_product_detail_add_cart))
            .check(matches(isDisplayed()))

        onView(withId(R.id.btn_cart_quantity_plus))
            .perform(click())

        onView(withId(R.id.btn_product_detail_add_cart))
            .check(matches(isEnabled()))
    }
}
