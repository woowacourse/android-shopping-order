package woowacourse.shopping.data.defaultRepository

import woowacourse.shopping.data.dataSource.ShoppingCartDataSource
import woowacourse.shopping.data.mapper.ShoppingCartMapper.toCartProduct
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.util.WoowaResult

class DefaultShoppingCartRepository(
    private val shoppingCartDataSource: ShoppingCartDataSource,
) : ShoppingCartRepository {
    override fun fetchAll(callback: (WoowaResult<List<CartProduct>>) -> Unit) {
        shoppingCartDataSource.fetchAll { result ->
            when (result) {
                is WoowaResult.SUCCESS -> callback(WoowaResult.SUCCESS(result.data.map { it.toCartProduct() }))
                is WoowaResult.FAIL -> callback(result)
            }
        }
    }

    override fun delete(callback: (WoowaResult<Boolean>) -> Unit, id: Long) {
        shoppingCartDataSource.delete(
            callback = { callback(it) },
            id = id,
        )
    }

    override fun insert(callback: (WoowaResult<Long>) -> Unit, productId: Long, quantity: Int) {
        shoppingCartDataSource.insert(
            callback = { result -> callback(result) },
            productId = productId,
            quantity = quantity,
        )
    }

    override fun update(
        callback: (WoowaResult<Boolean>) -> Unit,
        productId: Long,
        updatedQuantity: Int,
    ) {
        shoppingCartDataSource.update(
            callback = { callback(it) },
            productId = productId,
            updatedQuantity = updatedQuantity,
        )
    }
}
