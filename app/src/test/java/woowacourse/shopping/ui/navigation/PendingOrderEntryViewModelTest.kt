@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.ui.navigation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.cart.SelectedCartOrder
import woowacourse.shopping.domain.model.cart.SelectedCartOrderItem
import woowacourse.shopping.domain.session.PendingOrderSessionManager

class PendingOrderEntryViewModelTest {
    @Test
    fun `저장된 주문 세션이 있으면 주문 화면 이동 이벤트를 발행한다`() {
        val viewModel =
            PendingOrderEntryViewModel(
                pendingOrderSessionManager =
                    FakePendingOrderSessionManager(
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
                pendingOrderSessionManager = FakePendingOrderSessionManager(),
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
                pendingOrderSessionManager = FakePendingOrderSessionManager(),
            )

        viewModel.handlePendingOrderEntryRequest(token = 1L)
        viewModel.consumePendingOrderEntryAction()
        viewModel.handlePendingOrderEntryRequest(token = 1L)

        assertEquals(null, viewModel.pendingOrderEntryAction.value)
    }

    private class FakePendingOrderSessionManager(
        private var pendingOrder: SelectedCartOrder? = null,
    ) : PendingOrderSessionManager {
        override fun start(order: SelectedCartOrder) {
            pendingOrder = order
        }

        override fun restore(): SelectedCartOrder? = pendingOrder

        override fun clear() {
            pendingOrder = null
        }

        override fun hasActiveSession(): Boolean = pendingOrder != null
    }
}
