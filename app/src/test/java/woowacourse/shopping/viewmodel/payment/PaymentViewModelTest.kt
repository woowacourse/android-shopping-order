package woowacourse.shopping.viewmodel.payment

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.coupon.FixedCoupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.fixture.CoroutinesTestExtension
import woowacourse.shopping.fixture.FakeCouponRepository
import woowacourse.shopping.view.payment.PaymentViewModel
import woowacourse.shopping.viewmodel.InstantTaskExecutorExtension
import woowacourse.shopping.viewmodel.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class PaymentViewModelTest {
    private lateinit var viewModel: PaymentViewModel
    private lateinit var couponRepository: CouponRepository
    private lateinit var orderRepository: OrderRepository
    val selectedProducts: List<CartProduct> =
        listOf(
            CartProduct(
                id = 1,
                product =
                    Product(
                        id = 1,
                        imageUrl = "",
                        name = "hwannow",
                        price = 20000,
                    ),
                quantity = 3,
            ),
            CartProduct(
                id = 2,
                product =
                    Product(
                        id = 1,
                        imageUrl = "",
                        name = "hwannow2",
                        price = 1000000,
                    ),
                quantity = 3,
            ),
        )

    @BeforeEach
    fun setUp() {
        couponRepository = FakeCouponRepository()
        orderRepository = mockk(relaxed = true)
        viewModel = PaymentViewModel(couponRepository, orderRepository)
        viewModel.initSelectedProducts(selectedProducts)
    }

    @Test
    fun `쿠폰을 선택하면 총 결제 금액이 변경된다`() {
        // given
        val coupon =
            FixedCoupon(
                id = 1,
                code = "FIXED5000",
                description = "5,000원 할인 쿠폰",
                expirationDate = "2024-11-30",
                discount = 5000,
                minimumAmount = 100,
            )

        // when
        viewModel.selectCoupon(coupon)
        val actual = viewModel.paymentDetail.getOrAwaitValue().totalPayment

        // then
        actual shouldBe 3058000
    }

    @Test
    fun `결제하면 주문이 완료된다`() {
        // given
        val selectedIds = slot<List<Int>>()
        coEvery { orderRepository.submitOrder(capture(selectedIds)) } returns Result.success(Unit)

        // when
        viewModel.submitOrder()

        // then
        coVerify { orderRepository.submitOrder(any()) }
        selectedIds.captured shouldBe listOf(1, 2)
    }
}
