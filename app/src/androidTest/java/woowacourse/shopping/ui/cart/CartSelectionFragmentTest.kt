package woowacourse.shopping.ui.cart

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.local.ShoppingCartDataBase
import woowacourse.shopping.data.order.remote.RemoteOrderRepository
import woowacourse.shopping.data.product.remote.RemoteProductRepository
import woowacourse.shopping.data.recent.local.RoomRecentProductRepository

@RunWith(AndroidJUnit4::class)
class CartSelectionFragmentTest {
    @Before
    fun setUp() {
        val cartViewModel =
            CartViewModel(
                RemoteProductRepository,
                RoomRecentProductRepository.getInstance(
                    ShoppingCartDataBase.getInstance(ApplicationProvider.getApplicationContext())
                        .recentProductDao(),
                ),
                RemoteCartRepository,
                RemoteOrderRepository,
            )
        val cartFragmentFactory = CartFragmentFactory(cartViewModel)
        launchFragmentInContainer<CartSelectionFragment>(factory = cartFragmentFactory)
    }

    @Test
    fun `장바구니_상품_목록이_보인다`() {
        onView(withId(R.id.rv_cart))
            .check(matches(isDisplayed()))
    }
}
