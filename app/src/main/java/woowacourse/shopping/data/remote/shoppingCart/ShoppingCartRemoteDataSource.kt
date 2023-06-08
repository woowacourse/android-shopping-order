package woowacourse.shopping.data.remote.shoppingCart

import woowacourse.shopping.data.dataSource.ShoppingCartDataSource
import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.data.remote.dto.request.InsertingProductDto
import woowacourse.shopping.data.remote.dto.request.UpdatingProductDto
import woowacourse.shopping.data.remote.dto.response.ShoppingCartDto
import woowacourse.shopping.domain.util.Error
import woowacourse.shopping.domain.util.WoowaResult
import woowacourse.shopping.util.fetchHeaderId
import woowacourse.shopping.util.fetchResponseBody

class ShoppingCartRemoteDataSource : ShoppingCartDataSource {
    override fun fetchAll(callback: (WoowaResult<List<ShoppingCartDto>>) -> Unit) {
        ServicePool.retrofitService.getAllCartItems().fetchResponseBody(
            onSuccess = { callback(WoowaResult.SUCCESS(it)) },
            onFailure = { callback(WoowaResult.FAIL(it)) },
        )
    }

    override fun delete(callback: (WoowaResult<Boolean>) -> Unit, id: Long) {
        ServicePool.retrofitService.deleteCartItem(id).fetchResponseBody(
            onSuccess = { callback(WoowaResult.SUCCESS(true)) },
            onFailure = {
                when (it) {
                    is Error.ResponseBodyNull -> callback(WoowaResult.SUCCESS(true))
                    else -> callback(WoowaResult.FAIL(it))
                }
            },
        )
    }

    override fun insert(callback: (WoowaResult<Long>) -> Unit, productId: Long, quantity: Int) {
        ServicePool.retrofitService.insertCartItem(InsertingProductDto(productId, quantity))
            .fetchHeaderId(
                onSuccess = { callback(WoowaResult.SUCCESS(it)) },
                onFailure = { callback(WoowaResult.FAIL(it)) },
            )
    }

    override fun update(
        callback: (WoowaResult<Boolean>) -> Unit,
        id: Long,
        updatedQuantity: Int,
    ) {
        ServicePool.retrofitService
            .updateCartItem(id, UpdatingProductDto(updatedQuantity))
            .fetchResponseBody(
                onSuccess = { callback(WoowaResult.SUCCESS(true)) },
                onFailure = { callback(WoowaResult.FAIL(it)) },
            )
    }
}
