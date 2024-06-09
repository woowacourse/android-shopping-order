package woowacourse.shopping.presentation.ui.detail

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R
import woowacourse.shopping.data.remote.injector.CartItemRepositoryInjector
import woowacourse.shopping.data.remote.injector.CouponRepositoryInjector
import woowacourse.shopping.data.remote.injector.OrderRepositoryInjector
import woowacourse.shopping.data.remote.injector.ProductRepositoryInjector
import woowacourse.shopping.presentation.ui.FakeCartItemRepository
import woowacourse.shopping.presentation.ui.FakeCouponRepository
import woowacourse.shopping.presentation.ui.FakeOrderRepository
import woowacourse.shopping.presentation.ui.FakeProductRepository
import woowacourse.shopping.presentation.ui.cart.CartActivity
import woowacourse.shopping.presentation.ui.cartProduct
import woowacourse.shopping.presentation.ui.detail.ProductDetailActivity.Companion.EXTRA_CART_PRODUCT
import java.lang.IllegalStateException

@RunWith(AndroidJUnit4::class)
class ProductEntityDetailActivityTest {
    private val intent =
        Intent(
            ApplicationProvider.getApplicationContext(),
            ProductDetailActivity::class.java,
        ).apply { putExtra(EXTRA_CART_PRODUCT, cartProduct) }

    @Before
    fun setUp() {
        ProductRepositoryInjector.setInstance(FakeProductRepository())
        CartItemRepositoryInjector.setInstance(FakeCartItemRepository())
        CouponRepositoryInjector.setInstance(FakeCouponRepository())
        OrderRepositoryInjector.setInstance(FakeOrderRepository())
    }

    @Test
    fun `선택된_상품의_이미지가_보인다`() {
        ActivityScenario.launch<CartActivity>(intent)
        onView(withId(R.id.iv_product)).check(
            matches(isDisplayed()),
        )
    }

    @Test
    fun `선택된_상품의_제목이_보인다`() {
        ActivityScenario.launch<CartActivity>(intent)
        onView(withId(R.id.tv_name)).check(
            matches(isDisplayed()),
        )
    }

    @Test
    fun `선택된_상품의_가격이_보인다`() {
        ActivityScenario.launch<CartActivity>(intent)
        onView(withId(R.id.tv_price_value)).check(
            matches(isDisplayed()),
        )
    }

    @Test
    fun `장바구니에_담으면_상품이_데이터에_추가된다`() =
        runTest {
            ActivityScenario.launch<CartActivity>(intent)
            CartItemRepositoryInjector.instance.getCartItems(0, 100).onSuccess {
                it.size shouldBe 51 // 디폴트 갯수
            }.onFailure {
                throw IllegalStateException()
            }
            onView(withId(R.id.tv_add_cart)).perform(click())
            CartItemRepositoryInjector.instance.getCartItems(0, 100).onSuccess {
                it.size shouldBe 52 // 하나 증가한 갯수
            }.onFailure {
                throw IllegalStateException()
            }
        }
}
