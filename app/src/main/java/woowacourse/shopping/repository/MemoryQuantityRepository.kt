package woowacourse.shopping.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.Quantity

class MemoryQuantityRepository(
    products: List<Product>,
) : QuantityRepository {
    private val quantityByProductId: MutableMap<Long, Quantity> =
        products
            .associate { product -> product.id to Quantity() }
            .toMutableMap()

    private val _quantities: MutableStateFlow<Map<Long, Int>> = MutableStateFlow(emptyMap())
    override val quantities: StateFlow<Map<Long, Int>> = _quantities

    init {
        syncQuantities()
    }

    override fun getQuantity(productId: Long): Int = getProductQuantity(productId).getQuantity()

    override fun plusQuantity(productId: Long) {
        getProductQuantity(productId).plusQuantity()
        syncQuantities()
    }

    override fun minusQuantity(productId: Long) {
        getProductQuantity(productId).minusQuantity()
        syncQuantities()
    }

    private fun getProductQuantity(productId: Long): Quantity =
        quantityByProductId[productId]
            ?: throw IllegalArgumentException("해당 상품을 찾을 수 없습니다.")

    private fun syncQuantities() {
        _quantities.value = quantityByProductId.mapValues { (_, quantity) -> quantity.getQuantity() }
    }
}
