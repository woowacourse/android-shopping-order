package woowacourse.shopping.source

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.cart.CartItemDataSource
import woowacourse.shopping.domain.model.ProductIdsCount

// TODO: 이거 data 타입 바뀜
class FakeShoppingCartProductIdDataSource(
    private val data: MutableList<ProductIdsCountData> = mutableListOf(),
) : CartItemDataSource {
    override fun findByProductId(productId: Long): ProductIdsCountData? = data.find { it.productId == productId }

    override fun loadPaged(page: Int): List<ProductIdsCountData> {
        TODO("Not yet implemented")
    }

    override fun loadAll(): List<ProductIdsCountData> = data

    override fun isFinalPage(page: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun addedNewProductsId(productIdsCount: ProductIdsCount): Long {
        TODO("Not yet implemented")
    }

    override fun removedProductsId(productId: Long): Long {
        val foundItem = data.find { it.productId == productId } ?: throw NoSuchElementException()
        data.remove(foundItem)
        return foundItem.productId
    }

    override fun plusProductsIdCount(productId: Long) {
        val oldItem = data.find { it.productId == productId } ?: throw NoSuchElementException()
        data.remove(oldItem)
        data.add(oldItem.copy(quantity = oldItem.quantity + 1))
    }

    override fun minusProductsIdCount(productId: Long) {
        val oldItem = data.find { it.productId == productId } ?: throw NoSuchElementException()
        data.remove(oldItem)
        data.add(oldItem.copy(quantity = oldItem.quantity - 1))
    }

    override fun clearAll() {
        data.clear()
    }
}
