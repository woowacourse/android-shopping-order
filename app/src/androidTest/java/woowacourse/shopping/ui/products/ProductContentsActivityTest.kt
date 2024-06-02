package woowacourse.shopping.ui.products

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R

@RunWith(AndroidJUnit4::class)
class ProductContentsActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(ProductContentsActivity::class.java)

    @Test
    fun `화면이_띄워지면_상품이_보인다`() {
        onView(allOf(withId(R.id.rv_products)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `스크롤이_가장_아래로_내려가면_더보기_버튼이_보인다`() {
        onView(withId(R.id.btn_load_more))
            .perform(nestedScrollTo())
            .check(matches(isDisplayed()))
            .check(matches(allOf(withText("상품 더보기"), isDisplayed())))
    }

    @Test
    fun `더보기_버튼을_누르면_상품이_로드된다`() {
        onView(withId(R.id.btn_load_more))
            .perform(nestedScrollTo())
            .perform(click())
        Thread.sleep(1100) // skeleton ui확인을 위한 텀

        onView(withId(R.id.rv_products))
            .check(matches(hasMinimumChildCount(40)))
    }
}
