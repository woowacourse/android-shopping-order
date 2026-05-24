package woowacourse.shopping.ui.payment

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.testing.MainDispatcherExtension
import woowacourse.shopping.testing.fakes.FakeCartRepository

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private lateinit var fakeCartRepository: FakeCartRepository

    private val products =
        listOf(
            Product(id = 1L, name = "커비", price = 100_000, imageUri = "uri1", category = "크루"),
            Product(id = 2L, name = "하로", price = 52_100, imageUri = "uri2", category = "크루"),
        )

    @BeforeEach
    fun init() {
        fakeCartRepository = FakeCartRepository()
    }

    @Test
    fun `선택한 장바구니 상품의 주문 금액을 계산한다`() = runTest {
        fakeCartRepository.insert(PurchaseProduct(id = 101L, product = products[0], count = 2))
        fakeCartRepository.insert(PurchaseProduct(id = 102L, product = products[1], count = 1))

        val viewModel =
            PaymentViewModel(
                cartRepository = fakeCartRepository,
                selectedCartItemIds = listOf(101L, 102L),
            )
        advanceUntilIdle()

        viewModel.payment.value.orderAmount shouldBe 252_100
    }

    @Test
    fun `결제를 완료하면 선택한 장바구니 상품을 삭제한다`() = runTest {
        fakeCartRepository.insert(PurchaseProduct(id = 101L, product = products[0], count = 1))
        fakeCartRepository.insert(PurchaseProduct(id = 102L, product = products[1], count = 1))
        val viewModel =
            PaymentViewModel(
                cartRepository = fakeCartRepository,
                selectedCartItemIds = listOf(101L),
            )
        advanceUntilIdle()

        viewModel.completePayment(onSuccess = {})
        advanceUntilIdle()

        fakeCartRepository.getProductCount() shouldBe 1
    }
}
