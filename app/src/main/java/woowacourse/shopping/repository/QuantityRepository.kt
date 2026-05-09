package woowacourse.shopping.repository

import kotlinx.coroutines.flow.StateFlow

interface QuantityRepository {
    val quantities: StateFlow<Map<Long, Int>>

    fun getQuantity(productId: Long): Int

    fun plusQuantity(productId: Long)

    fun minusQuantity(productId: Long)

}
