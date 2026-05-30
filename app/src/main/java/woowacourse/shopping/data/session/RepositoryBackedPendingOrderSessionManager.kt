package woowacourse.shopping.data.session

import woowacourse.shopping.domain.model.cart.SelectedCartOrder
import woowacourse.shopping.domain.repository.PendingOrderRepository
import woowacourse.shopping.domain.session.PendingOrderSessionManager

class RepositoryBackedPendingOrderSessionManager(
    private val pendingOrderRepository: PendingOrderRepository,
) : PendingOrderSessionManager {
    override fun start(order: SelectedCartOrder) {
        pendingOrderRepository.savePendingOrder(order)
    }

    override fun restore(): SelectedCartOrder? = pendingOrderRepository.getPendingOrder()

    override fun clear() {
        pendingOrderRepository.clearPendingOrder()
    }

    override fun hasActiveSession(): Boolean = pendingOrderRepository.getPendingOrder() != null
}
