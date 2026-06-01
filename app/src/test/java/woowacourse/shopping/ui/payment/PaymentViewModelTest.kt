package woowacourse.shopping.ui.payment

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedAmountCoupon
import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.testing.MainDispatcherExtension
import woowacourse.shopping.testing.fakes.FakeCartRepository
import woowacourse.shopping.ui.event.UiEvent
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private lateinit var fakeCartRepository: FakeCartRepository
    private lateinit var fakeCouponRepository: FakeCouponRepository
    private lateinit var fakeOrderRepository: FakeOrderRepository

    private val products =
        listOf(
            Product(id = 1L, name = "커비", price = 100_000, imageUri = "uri1", category = "크루"),
            Product(id = 2L, name = "하로", price = 52_100, imageUri = "uri2", category = "크루"),
        )

    @BeforeEach
    fun init() {
        fakeCartRepository = FakeCartRepository()
        fakeCouponRepository =
            FakeCouponRepository(
                coupons =
                    listOf(
                        FixedAmountCoupon(
                            code = "FIXED5000",
                            name = "5,000원 할인 쿠폰",
                            expirationDate = LocalDate.of(2026, 11, 30),
                            discountAmount = 5_000,
                            minimumOrderAmount = 100_000,
                        ),
                    ),
            )
        fakeOrderRepository = FakeOrderRepository()
    }

    @Test
    fun `선택한 장바구니 상품의 주문 금액을 계산한다`() =
        runTest {
            fakeCartRepository.insert(PurchaseProduct(id = 101L, product = products[0], count = 2))
            fakeCartRepository.insert(PurchaseProduct(id = 102L, product = products[1], count = 1))

            val viewModel =
                createViewModel(selectedCartItemIds = listOf(101L, 102L))
            advanceUntilIdle()

            viewModel.payment.value.orderAmount shouldBe 252_100
        }

    @Test
    fun `결제 화면 진입 시 쿠폰 목록을 조회하고 조건에 맞는 첫 번째 쿠폰을 선택한다`() =
        runTest {
            fakeCartRepository.insert(PurchaseProduct(id = 101L, product = products[0], count = 1))
            val viewModel = createViewModel(selectedCartItemIds = listOf(101L))

            advanceUntilIdle()

            viewModel.coupons.value.size shouldBe 1
            viewModel.payment.value.selectedCoupon
                ?.code shouldBe "FIXED5000"
        }

    @Test
    fun `조건에 맞지 않는 쿠폰은 선택되지 않는다`() =
        runTest {
            fakeCartRepository.insert(PurchaseProduct(id = 102L, product = products[1], count = 1))
            val viewModel = createViewModel(selectedCartItemIds = listOf(102L))
            advanceUntilIdle()

            viewModel.selectCoupon("FIXED5000")

            viewModel.payment.value.selectedCoupon shouldBe null
        }

    @Test
    fun `결제를 완료하면 선택한 장바구니 상품으로 주문을 생성한다`() =
        runTest {
            fakeCartRepository.insert(PurchaseProduct(id = 101L, product = products[0], count = 1))
            fakeCartRepository.insert(PurchaseProduct(id = 102L, product = products[1], count = 1))
            val viewModel = createViewModel(selectedCartItemIds = listOf(101L))
            advanceUntilIdle()

            viewModel.completePayment(onSuccess = {})
            advanceUntilIdle()

            fakeOrderRepository.createdOrderCartItemIds shouldBe listOf(101L)
            fakeCartRepository.getProductCount() shouldBe 2
        }

    @Test
    fun `이벤트 수집자가 없어도 결제 완료 메시지를 보관한다`() =
        runTest {
            fakeCartRepository.insert(PurchaseProduct(id = 101L, product = products[0], count = 1))
            val viewModel = createViewModel(selectedCartItemIds = listOf(101L))
            advanceUntilIdle()

            viewModel.completePayment(onSuccess = {})
            advanceUntilIdle()

            viewModel.uiEvent.first() shouldBe UiEvent.ShowMessage("주문이 완료되었습니다.")
        }

    @Test
    fun `처리한 이벤트는 다시 수집되지 않는다`() =
        runTest {
            fakeCartRepository.insert(PurchaseProduct(id = 101L, product = products[0], count = 1))
            val viewModel = createViewModel(selectedCartItemIds = listOf(101L))
            advanceUntilIdle()

            viewModel.completePayment(onSuccess = {})
            advanceUntilIdle()
            viewModel.uiEvent.first() shouldBe UiEvent.ShowMessage("주문이 완료되었습니다.")

            withTimeoutOrNull(100) { viewModel.uiEvent.first() } shouldBe null
        }

    private fun createViewModel(selectedCartItemIds: List<Long>): PaymentViewModel =
        PaymentViewModel(
            cartRepository = fakeCartRepository,
            couponRepository = fakeCouponRepository,
            orderRepository = fakeOrderRepository,
            selectedCartItemIds = selectedCartItemIds,
        )
}

private class FakeCouponRepository(
    private val coupons: List<Coupon>,
) : CouponRepository {
    override suspend fun getCoupons(): List<Coupon> = coupons
}

private class FakeOrderRepository : OrderRepository {
    var createdOrderCartItemIds: List<Long> = emptyList()
        private set

    override suspend fun createOrder(cartItemIds: List<Long>) {
        createdOrderCartItemIds = cartItemIds
    }
}
