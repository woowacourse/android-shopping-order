package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.CartRemoteDataSource
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.ProductIdBody
import woowacourse.shopping.model.QuantityBody
import woowacourse.shopping.utils.RetrofitUtil

class CartRemoteDataSourceImpl : CartRemoteDataSource {
    private var credentials = "Basic YUBhLmNvbToxMjM0"

    override fun getAll(callback: (Result<List<CartProduct>>) -> Unit) {
        RetrofitUtil.retrofitCartService
            .getCarts(credentials)
            .enqueue(RetrofitUtil.callback(callback))
    }

    override fun postItem(itemId: Int, callback: (Result<Int>) -> Unit) {
        RetrofitUtil.retrofitCartService
            .postCart(credentials, ProductIdBody(itemId))
            .enqueue(
                RetrofitUtil.callbackWithNoBody { result ->
                    result.onSuccess { callback(Result.success(1)) }
                        .onFailure { e -> callback(Result.failure(e)) }
                }
            )
    }

    override fun patchItemQuantity(itemId: Int, quantity: Int, callback: (Result<Int>) -> Unit) {
        RetrofitUtil.retrofitCartService
            .patchCart(itemId, credentials, QuantityBody(quantity))
            .enqueue(
                RetrofitUtil.callbackWithNoBody { result ->
                    result.onSuccess { callback(Result.success(quantity)) }
                        .onFailure { e -> callback(Result.failure(e)) }
                }
            )
    }

    override fun deleteItem(itemId: Int, callback: (Result<Int>) -> Unit) {
        RetrofitUtil.retrofitCartService
            .deleteCart(itemId, credentials)
            .enqueue(
                RetrofitUtil.callbackWithNoBody { result ->
                    result.onSuccess { callback(Result.success(0)) }
                        .onFailure { e -> callback(Result.failure(e)) }
                }
            )
    }
}
