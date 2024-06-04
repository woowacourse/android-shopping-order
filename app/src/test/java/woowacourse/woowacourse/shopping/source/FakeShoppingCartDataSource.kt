package woowacourse.shopping.source

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.model.CartItemDto

// TODO: 이거 data 타입 바뀜
class FakeShoppingCartDataSource(
    private val data: MutableList<ProductIdsCountData> = mutableListOf(),
) : ShoppingCartDataSource {
    override fun findByProductId(productId: Long): ProductIdsCountData? = data.find { it.productId == productId }
    override fun loadAllCartItems(): List<CartItemDto> {
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

    override fun plusProductsIdCount(cartItemId: Long, quantity: Int) {
        TODO("Not yet implemented")
    }

    override fun minusProductsIdCount(cartItemId: Long, quantity: Int) {
        TODO("Not yet implemented")
    }
}
