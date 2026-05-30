@file:Suppress("NonAsciiCharacters")

package woowacourse.shopping.data.session

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import woowacourse.shopping.domain.model.cart.SelectedCartOrder
import woowacourse.shopping.domain.model.cart.SelectedCartOrderItem
import woowacourse.shopping.domain.repository.PendingOrderRepository

class RepositoryBackedPendingOrderSessionManagerTest {
    @Test
    fun `세션 시작 시 주문을 저장한다`() {
        val repository = FakePendingOrderRepository()
        val sessionManager = RepositoryBackedPendingOrderSessionManager(repository)
        val order = selectedCartOrder()

        sessionManager.start(order)

        assertEquals(order, repository.getPendingOrder())
        assertTrue(sessionManager.hasActiveSession())
    }

    @Test
    fun `세션 복원 시 저장된 주문을 반환한다`() {
        val repository = FakePendingOrderRepository()
        val sessionManager = RepositoryBackedPendingOrderSessionManager(repository)
        val order = selectedCartOrder()
        repository.savePendingOrder(order)

        assertEquals(order, sessionManager.restore())
    }

    @Test
    fun `세션 종료 시 저장된 주문을 삭제한다`() {
        val repository = FakePendingOrderRepository()
        val sessionManager = RepositoryBackedPendingOrderSessionManager(repository)
        sessionManager.start(selectedCartOrder())

        sessionManager.clear()

        assertEquals(null, repository.getPendingOrder())
        assertFalse(sessionManager.hasActiveSession())
    }

    private fun selectedCartOrder(): SelectedCartOrder =
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
        )

    private class FakePendingOrderRepository : PendingOrderRepository {
        private var pendingOrder: SelectedCartOrder? = null

        override fun getPendingOrder(): SelectedCartOrder? = pendingOrder

        override fun savePendingOrder(order: SelectedCartOrder) {
            pendingOrder = order
        }

        override fun clearPendingOrder() {
            pendingOrder = null
        }
    }
}
