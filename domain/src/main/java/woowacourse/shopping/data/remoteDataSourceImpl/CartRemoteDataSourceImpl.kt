package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.CartRemoteDataSource
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.ProductIdBody
import woowacourse.shopping.model.QuantityBody
import woowacourse.shopping.utils.RetrofitUtil

class CartRemoteDataSourceImpl : CartRemoteDataSource {
    private var credentials = "Basic YUBhLmNvbToxMjM0"

    override fun getAll(): Result<List<CartProduct>> = runCatching {
        RetrofitUtil.retrofitCartService.getCarts(credentials).execute().body()!!
    }

    override fun postItem(itemId: Int): Result<Int> = runCatching {
        RetrofitUtil.retrofitCartService.postCart(credentials, ProductIdBody(itemId)).execute()
        1
    }

    override fun patchItemQuantity(itemId: Int, quantity: Int): Result<Int> = runCatching {
        RetrofitUtil.retrofitCartService
            .patchCart(itemId, credentials, QuantityBody(quantity)).execute()
        quantity
    }

    override fun deleteItem(itemId: Int): Result<Int> = runCatching {
        RetrofitUtil.retrofitCartService.deleteCart(itemId, credentials).execute()
        0
    }
}
