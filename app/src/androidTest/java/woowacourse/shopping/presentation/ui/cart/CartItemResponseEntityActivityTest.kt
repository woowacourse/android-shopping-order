package woowacourse.shopping.presentation.ui.cart

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
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

@RunWith(AndroidJUnit4::class)
class CartItemResponseEntityActivityTest {
    @Before
    fun setUp() {
        ProductRepositoryInjector.setInstance(FakeProductRepository())
        CartItemRepositoryInjector.setInstance(FakeCartItemRepository())
        CouponRepositoryInjector.setInstance(FakeCouponRepository())
        OrderRepositoryInjector.setInstance(FakeOrderRepository())
    }

    @Test
    fun `장바구니에_아이템_리스트가_보여진다`() {
        ActivityScenario.launch(CartActivity::class.java)
        onView(ViewMatchers.withId(R.id.rv_carts))
            .check(matches(isDisplayed()))
    }
}
