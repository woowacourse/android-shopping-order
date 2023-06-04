package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.CartRemoteDataSource
import woowacourse.shopping.dto.CartProductDto
import woowacourse.shopping.dto.ProductIdDto
import woowacourse.shopping.dto.QuantityDto
import woowacourse.shopping.utils.RetrofitUtil

class CartRemoteDataSourceImpl : CartRemoteDataSource {
    override fun getAll(): Result<List<CartProductDto>> = runCatching {
        RetrofitUtil.getInstance().retrofitCartService
            .getCarts().execute().body()!!
    }

    override fun postItem(itemId: Int): Result<Int> = runCatching {
        RetrofitUtil.getInstance().retrofitCartService
            .postCart(ProductIdDto(itemId)).execute().headers()["location"]!!
            .split("/").last().toInt()
    }

    override fun patchItemQuantity(itemId: Int, quantity: Int): Result<Int> = runCatching {
        RetrofitUtil.getInstance().retrofitCartService
            .patchCart(itemId, QuantityDto(quantity)).execute()
        quantity
    }

    override fun deleteItem(itemId: Int): Result<Int> = runCatching {
        RetrofitUtil.getInstance().retrofitCartService
            .deleteCart(itemId).execute()
        0
    }
}
