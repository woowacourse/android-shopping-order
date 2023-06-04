package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.CartRemoteDataSource
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.model.ProductIdBody
import woowacourse.shopping.model.QuantityBody
import woowacourse.shopping.utils.RetrofitUtil

class CartRemoteDataSourceImpl : CartRemoteDataSource {
    override fun getAll(): Result<List<CartProduct>> = runCatching {
        RetrofitUtil.getInstance().retrofitCartService
            .getCarts().execute().body()!!
    }

    override fun postItem(itemId: Int): Result<Int> = runCatching {
        RetrofitUtil.getInstance().retrofitCartService
            .postCart(ProductIdBody(itemId)).execute()
        1
    }

    override fun patchItemQuantity(itemId: Int, quantity: Int): Result<Int> = runCatching {
        RetrofitUtil.getInstance().retrofitCartService
            .patchCart(itemId, QuantityBody(quantity)).execute()
        quantity
    }

    override fun deleteItem(itemId: Int): Result<Int> = runCatching {
        RetrofitUtil.getInstance().retrofitCartService
            .deleteCart(itemId).execute()
        0
    }
}
