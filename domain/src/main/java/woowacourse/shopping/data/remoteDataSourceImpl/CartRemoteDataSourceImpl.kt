package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.client.RetrofitClient
import woowacourse.shopping.data.remoteDataSource.CartRemoteDataSource
import woowacourse.shopping.dto.CartProductDto
import woowacourse.shopping.dto.ProductIdDto
import woowacourse.shopping.dto.QuantityDto

class CartRemoteDataSourceImpl : CartRemoteDataSource {
    override fun getAll(): Result<List<CartProductDto>> = runCatching {
        RetrofitClient.getInstance().retrofitCartService
            .getCarts().execute().body()!!
    }

    override fun postItem(itemId: Int): Result<Int> = runCatching {
        RetrofitClient.getInstance().retrofitCartService
            .postCart(ProductIdDto(itemId)).execute().headers()["location"]!!
            .split("/").last().toInt()
    }

    override fun patchItemQuantity(itemId: Int, quantity: Int): Result<Int> = runCatching {
        RetrofitClient.getInstance().retrofitCartService
            .patchCart(itemId, QuantityDto(quantity)).execute()
        quantity
    }

    override fun deleteItem(itemId: Int): Result<Int> = runCatching {
        RetrofitClient.getInstance().retrofitCartService
            .deleteCart(itemId).execute()
        0
    }
}
