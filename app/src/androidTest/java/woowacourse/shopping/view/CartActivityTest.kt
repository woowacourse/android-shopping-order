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
import woowacourse.shopping.checkHasDescendantWithIdDisplayed
import woowacourse.shopping.checkIsDisplayed
import woowacourse.shopping.presentation.cart.CartActivity
import woowacourse.shopping.scrollToPosition

class CartActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(CartActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @Test
    fun 툴바에_타이틀이_표시된다() {
        onView(withText(R.string.cart_toolbar_text)).checkIsDisplayed()
    }

    @Test
    fun 장바구니_목록이_표시된다() {
        onView(withId(R.id.rv_cart_product)).checkIsDisplayed()
    }

    @Test
    fun `장바구니_아이템에는_체크박스가_표시된다`() {
        onView(withId(R.id.rv_cart_product))
            .scrollToPosition(0)
            .checkHasDescendantWithIdDisplayed(R.id.checkbox_cart_item)
    }

    @Test
    fun `장바구니_아이템에는_상품_이름이_표시된다`() {
        onView(withId(R.id.rv_cart_product))
            .scrollToPosition(0)
            .checkHasDescendantWithIdDisplayed(R.id.tv_cart_product_name)
    }

    @Test
    fun `상품_아이템에는_이미지가_표시된다`() {
        onView(withId(R.id.rv_cart_product))
            .scrollToPosition(0)
            .checkHasDescendantWithIdDisplayed(R.id.iv_cart_product)
    }

    @Test
    fun `상품_아이템에는_총_가격이_표시된다`() {
        onView(withId(R.id.rv_cart_product))
            .scrollToPosition(0)
            .checkHasDescendantWithIdDisplayed(R.id.tv_cart_product_price)
    }

    @Test
    fun `상품_아이템에는_삭제_버튼이_표시된다`() {
        onView(withId(R.id.rv_cart_product))
            .scrollToPosition(0)
            .checkHasDescendantWithIdDisplayed(R.id.ib_cart_product_delete)
    }

    @Test
    fun 전체_체크버튼이_표시된다() {
        onView(withId(R.id.checkbox_all_check)).checkIsDisplayed()
    }

    @Test
    fun 선택한_상품_가격의_총_합이_표시된다() {
        onView(withId(R.id.tv_total_price)).checkIsDisplayed()
    }

    @Test
    fun 선택한_상품_가격의_수량이_표시된다() {
        onView(withId(R.id.layout_cart_recommend)).checkIsDisplayed()
    }

    @After
    fun finish() {
        Intents.release()
    }
}
