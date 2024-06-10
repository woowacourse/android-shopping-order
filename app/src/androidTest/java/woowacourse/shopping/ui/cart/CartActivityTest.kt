package woowacourse.shopping.ui.cart

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.impl.CartRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.impl.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.impl.RecentProductLocalDataSourceImpl
import woowacourse.shopping.data.db.recentproduct.RecentProductDatabase
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.data.service.NetworkModule
import woowacourse.shopping.ui.cart.adapter.CartViewHolder

@RunWith(AndroidJUnit4::class)
class CartActivityTest {
    private val recentProductRepository by lazy {
        RecentProductRepositoryImpl.get(
            RecentProductLocalDataSourceImpl(
                RecentProductDatabase.database().recentProductDao(),
            ),
        )
    }

    private val cartRepository by lazy {
        CartRepositoryImpl(
            CartRemoteDataSourceImpl(NetworkModule.cartItemService),
            OrderRemoteDataSourceImpl(NetworkModule.orderService),
        )
    }

    @get:Rule
    val activityRule = ActivityScenarioRule(CartActivity::class.java)

    @Before
    fun setUp() {
        cartRepository.postCartItems(2L, 1)
    }

    @Test
    fun `화면이_띄워지면_장바구니에_담긴_상품명이_보인다`() {
        onView(withId(R.id.rv_cart))
            .perform(RecyclerViewActions.scrollToPosition<CartViewHolder>(0))
            .check(matches(hasDescendant(allOf(withText("나이키"), isDisplayed()))))
    }

    @Test
    fun `화면이_띄워지면_장바구니에_담긴_상품의_가격이_보인다`() {
        onView(withId(R.id.rv_cart))
            .perform(RecyclerViewActions.scrollToPosition<CartViewHolder>(0))
            .check(matches(hasDescendant(allOf(withId(R.id.tv_product_price), isDisplayed()))))
            .check(matches(hasDescendant(allOf(withText("1,000원"), isDisplayed()))))
    }

    @Test
    fun `초기에_주문하기_버튼이_비활성화_되어있다`() {
        onView(withId(R.id.btn_order))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))
    }

    @Test
    fun `상품_선택_시_주문하기_버튼이_활성화_된다`() {
        // when
        onView(withId(R.id.cb_cart_item_check))
            .perform(click())

        // then
        onView(withId(R.id.btn_order))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
    }

    @Test
    fun `상품_선택_시_선택한_상품의_가격이_보인다`() {
        // when
        onView(withId(R.id.cb_cart_item_check))
            .perform(click())

        // then
        onView(withId(R.id.tv_total_price))
            .check(matches(isDisplayed()))
            .check(matches(withText("1,000원")))
    }

    @Test
    fun `상품_선택_후_주문하기_버튼을_누를_시_추천_상품이_보인다`() {
        // given
        recentProductRepository.insert(2L)

        // when
        onView(withId(R.id.cb_cart_item_check))
            .perform(click())

        onView(withId(R.id.btn_order))
            .perform(click())

        // then
        onView(withId(R.id.rv_recommend_product))
            .check(matches(isDisplayed()))
    }
}
