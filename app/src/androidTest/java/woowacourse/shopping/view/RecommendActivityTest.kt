package woowacourse.shopping.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.R
import woowacourse.shopping.checkIsDisplayed
import woowacourse.shopping.presentation.recommend.RecommendActivity

class RecommendActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(RecommendActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @Test
    fun 툴바에_Cart가_표시된다() {
        onView(withText(R.string.cart_toolbar_text)).checkIsDisplayed()
    }

    @Test
    fun 상품_추천_타이틀_메시지가_표시된다() {
        onView(withId(R.id.tv_recommend_title)).checkIsDisplayed()
    }

    @Test
    fun 상품_추천_설명_메시지가_표시된다() {
        onView(withId(R.id.tv_recommend_desc)).checkIsDisplayed()
    }

    @Test
    fun 추천_상품_목록이_표시된다() {
        onView(withId(R.id.rv_recommend)).checkIsDisplayed()
    }

    @Test
    fun 선택한_상품의_총_가격이_표시된다() {
        onView(withId(R.id.tv_total_price)).checkIsDisplayed()
    }

    @Test
    fun 선택한_상품의_수량이_표시된다() {
        onView(withId(R.id.layout_cart_recommend)).checkIsDisplayed()
    }

    @After
    fun finish() {
        Intents.release()
    }
}
