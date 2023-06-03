package woowacourse.shopping.data.cart

import woowacourse.shopping.data.Authorization
import woowacourse.shopping.data.entity.CartItemEntity
import woowacourse.shopping.data.entity.ProductIdEntity
import woowacourse.shopping.data.entity.QuantityEntity
import woowacourse.shopping.network.retrofit.CartItemRetrofitService

class CartItemRemoteSource(private val cartItemService: CartItemRetrofitService) :
    CartItemDataSource {
    override fun save(productId: Long, userToken: String): Result<Long> {
        return runCatching {
            val response =
                cartItemService.postCartItem(Authorization.KEY_FORMAT.format(userToken), ProductIdEntity(productId))
                    .execute()
            if (response.code() != 201) throw Throwable(response.errorBody()?.string())
            response.headers()["Location"]?.substringAfterLast("/")?.toLong() ?: throw Throwable(
                response.message()
            )
        }
    }

    override fun findAll(userToken: String): Result<List<CartItemEntity>> {
        return runCatching {
            val response = cartItemService.selectCartItems(Authorization.KEY_FORMAT.format(userToken)).execute()
            if (response.code() != 200) throw Throwable(response.message())
            response.body() ?: throw Throwable(response.message())
        }
    }

    override fun findAll(limit: Int, offset: Int, userToken: String): Result<List<CartItemEntity>> {
        return runCatching {
            val response = cartItemService.selectCartItems(Authorization.KEY_FORMAT.format(userToken)).execute()
            if (response.code() != 200) throw Throwable(response.message())
            val body = response.body() ?: throw Throwable(response.message())
            body.slice(offset until body.size).take(limit)
        }
    }

    override fun updateCountById(id: Long, count: Int, userToken: String): Result<Unit> {
        return runCatching {
            val response =
                cartItemService.updateCountCartItem(Authorization.KEY_FORMAT.format(userToken), id, QuantityEntity(count))
                    .execute()
            if (response.code() != 200) throw Throwable(response.message())
        }
    }

    override fun deleteById(id: Long, userToken: String): Result<Unit> {
        return runCatching {
            val response = cartItemService.deleteCartItem(Authorization.KEY_FORMAT.format(userToken), id).execute()
            if (response.code() != 204) throw Throwable(response.message())
        }
    }
}
