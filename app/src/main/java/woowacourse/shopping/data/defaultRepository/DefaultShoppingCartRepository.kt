package woowacourse.shopping.data.defaultRepository

import woowacourse.shopping.data.error.WoowaException
import woowacourse.shopping.data.mapper.ShoppingCartMapper.toCartProduct
import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.data.remote.dto.request.InsertingProductDto
import woowacourse.shopping.data.remote.dto.request.UpdatingProductDto
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.util.fetchHeaderId
import woowacourse.shopping.util.fetchResponseBody

class DefaultShoppingCartRepository : ShoppingCartRepository {
    override fun fetchAll(callback: (Result<List<CartProduct>>) -> Unit) {
        ServicePool.retrofitService.getAllCartItems().fetchResponseBody(
            onSuccess = { callback(Result.success(it.map { it.toCartProduct() })) },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun delete(callback: (Result<Boolean>) -> Unit, id: Long) {
        ServicePool.retrofitService.deleteCartItem(id).fetchResponseBody(
            onSuccess = { callback(Result.success(true)) },
            onFailure = {
                when (it) {
                    is WoowaException.ResponseBodyNull -> callback(Result.success(true))
                    else -> callback(Result.failure(it))
                }
            },
        )
    }

    override fun insert(callback: (Result<Long>) -> Unit, productId: Long, quantity: Int) {
        ServicePool.retrofitService.insertCartItem(InsertingProductDto(productId, quantity))
            .fetchHeaderId(
                onSuccess = { callback(Result.success(it)) },
                onFailure = { callback(Result.failure(it)) },
            )
    }

    override fun update(
        callback: (Result<Boolean>) -> Unit,
        id: Long,
        updatedQuantity: Int,
    ) {
        ServicePool.retrofitService.updateCartItem(id, UpdatingProductDto(updatedQuantity))
            .fetchResponseBody(
                onSuccess = { callback(Result.success(true)) },
                onFailure = { callback(Result.failure(it)) },
            )
    }
}
