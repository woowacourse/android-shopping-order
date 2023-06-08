package woowacourse.shopping.data.remote

import woowacourse.shopping.data.client.RetrofitClient
import woowacourse.shopping.dto.ProductIdDto
import woowacourse.shopping.dto.QuantityDto
import woowacourse.shopping.dto.toDomain
import woowacourse.shopping.model.CartProduct

class CartRetrofitDataSource : CartRemoteDataSource {
    override fun getAll(): Result<List<CartProduct>> = runCatching {
        RetrofitClient.getInstance().retrofitCartService
            .getCarts().execute().body()!!.toDomain()
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
