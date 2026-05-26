@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.ui.navigation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.cart.SelectedCartOrder
import woowacourse.shopping.domain.model.cart.SelectedCartOrderItem
import woowacourse.shopping.domain.repository.PendingOrderRepository

class PendingOrderEntryViewModelTest {
    @Test
    fun `저장된 주문 세션이 있으면 주문 화면 이동 이벤트를 발행한다`() {
        val viewModel =
            PendingOrderEntryViewModel(
                pendingOrderRepository =
                    FakePendingOrderRepository(
                        pendingOrder =
                            SelectedCartOrder(
                                items =
                                    listOf(
                                        SelectedCartOrderItem(
                                            cartItemId = 1L,
                                            productId = 1L,
                                            price = 10_000,
                                            quantity = 1,
                                        ),
                                    ),
                            ),
                    ),
            )

        viewModel.handlePendingOrderEntryRequest(token = 1L)

        assertEquals(
            PendingOrderEntryAction.OpenPendingOrder,
            viewModel.pendingOrderEntryAction.value,
        )
    }

    @Test
    fun `저장된 주문 세션이 없으면 이동 없이 처리 완료 이벤트를 발행한다`() {
        val viewModel =
            PendingOrderEntryViewModel(
                pendingOrderRepository = FakePendingOrderRepository(),
            )

        viewModel.handlePendingOrderEntryRequest(token = 1L)

        assertEquals(
            PendingOrderEntryAction.Ignore,
            viewModel.pendingOrderEntryAction.value,
        )
    }

    @Test
    fun `같은 토큰은 한 번만 처리한다`() {
        val viewModel =
            PendingOrderEntryViewModel(
                pendingOrderRepository = FakePendingOrderRepository(),
            )

        viewModel.handlePendingOrderEntryRequest(token = 1L)
        viewModel.consumePendingOrderEntryAction()
        viewModel.handlePendingOrderEntryRequest(token = 1L)

        assertEquals(null, viewModel.pendingOrderEntryAction.value)
    }

    private class FakePendingOrderRepository(
        private var pendingOrder: SelectedCartOrder? = null,
    ) : PendingOrderRepository {
        override fun getPendingOrder(): SelectedCartOrder? = pendingOrder

        override fun savePendingOrder(order: SelectedCartOrder) {
            pendingOrder = order
        }

        override fun clearPendingOrder() {
            pendingOrder = null
        }
    }
}
