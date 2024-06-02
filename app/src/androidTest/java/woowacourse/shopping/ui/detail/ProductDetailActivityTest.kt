package woowacourse.shopping.ui.detail

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R

@RunWith(AndroidJUnit4::class)
class ProductDetailActivityTest {
    private val intent =
        Intent(
            ApplicationProvider.getApplicationContext(),
            ProductDetailActivity::class.java,
        ).run {
            putExtra(ProductDetailKey.EXTRA_PRODUCT_KEY, 2L)
        }

    @get:Rule
    val activityRule = ActivityScenarioRule<ProductDetailActivity>(intent)

    @Test
    fun `화면이_띄워지면_상품명이_보인다`() {
        onView(withId(R.id.tv_product_name))
            .check(matches(isDisplayed()))
            .check(matches(withText("나이키")))
    }

    @Test
    fun `화면이_띄워지면_상품_가격이_보인다`() {
        onView(withId(R.id.tv_product_price))
            .check(matches(isDisplayed()))
            .check(matches(withText("0원")))
    }

    @Test
    fun `초기_화면이_띄워지면_장바구니_담기_버튼이_비활성화_된다`() {
        onView(withId(R.id.btn_add_product))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))
    }

    @Test
    fun `플러스_버튼을_누르면_가격이_올라간다`() {
        onView(withId(R.id.btn_plus_product))
            .perform(click())

        onView(withId(R.id.tv_product_price))
            .check(matches(isDisplayed()))
            .check(matches(withText("1,000원")))
    }

    @Test
    fun `수량이_증가하면_장바구니_담기_버튼이_활성화_된다`() {
        // when
        onView(withId(R.id.btn_plus_product))
            .perform(click())

        // then
        onView(withId(R.id.btn_add_product))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
    }

    @Test
    fun `마이너스_버튼을_누르면_가격이_올라간다`() {
        // given
        repeat(3) {
            onView(withId(R.id.btn_plus_product))
                .perform(click())
        }

        // when
        onView(withId(R.id.btn_minus_product))
            .perform(click())

        // then
        onView(withId(R.id.tv_product_price))
            .check(matches(isDisplayed()))
            .check(matches(withText("2,000원")))
    }
}
