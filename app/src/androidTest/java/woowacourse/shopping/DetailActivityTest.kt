package woowacourse.shopping

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.presentation.ui.detail.DetailActivity

@RunWith(AndroidJUnit4::class)
class DetailActivityTest {
    private val intent =
        DetailActivity.createIntent(
            ApplicationProvider.getApplicationContext(),
            2L,
        )

    @get:Rule
    val activityRule = ActivityScenarioRule<DetailActivity>(intent)

    @Test
    fun `선택한_상품의_사진이_표시된다`() {
        onView(withId(R.id.iv_detail_product_image))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `선택한_상품의_제목이_표시된다`() {
        onView(withId(R.id.tv_detail_product_name))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `선택한_상품의_가격이_표시된다`() {
        onView(withId(R.id.tv_detail_price))
            .check(matches(isDisplayed()))
    }

    @Test
    fun `장바구니_담기_버튼을_클릭하면_장바구니_페이지로_이동한다`() {
        onView(withId((R.id.btn_put_cart))).perform(click())
        onView(withId(R.id.activity_cart)).check(matches(isDisplayed()))
    }
}
