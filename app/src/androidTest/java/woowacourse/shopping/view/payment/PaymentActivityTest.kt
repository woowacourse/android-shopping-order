package woowacourse.shopping.view.payment

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.R
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.di.provider.RepositoryProvider
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.fixture.FakeCouponRepository
import woowacourse.shopping.fixture.FakeOrderRepository
import woowacourse.shopping.fixture.couponsFixture
import woowacourse.shopping.fixture.productsFixture
import woowacourse.shopping.presentation.view.payment.PaymentActivity
import woowacourse.shopping.util.nthProductInRecyclerView

class PaymentActivityTest {
    @BeforeEach
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val fakeCouponRepository = FakeCouponRepository()
        val fakeOrderRepository =
            FakeOrderRepository(
                cartProducts = productsFixture.take(5).map { CartProduct(it.id, it.toDomain(), 1) },
            )

        RepositoryProvider.initCouponRepository(fakeCouponRepository)
        RepositoryProvider.initOrderRepository(fakeOrderRepository)

        val intent = PaymentActivity.newIntent(context, productsFixture.take(5).map { it.id })
        ActivityScenario.launch<PaymentActivity>(intent)
    }

    @Test
    fun `사용_가능한_쿠폰_목록이_보여진다`() {
        firstProductInRecyclerView(R.id.text_view_coupon_description)
            .check(matches(withText(couponsFixture[0].description)))
    }

    private fun firstProductInRecyclerView(targetViewId: Int) =
        nthProductInRecyclerView(
            R.id.recycler_view_coupon,
            0,
            targetViewId,
        )
}
