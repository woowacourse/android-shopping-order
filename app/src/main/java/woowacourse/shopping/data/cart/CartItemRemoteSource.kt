package woowacourse.shopping.data.cart

import woowacourse.shopping.data.entity.CartItemEntity
import woowacourse.shopping.data.entity.ProductIdEntity
import woowacourse.shopping.data.entity.QuantityEntity
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.network.retrofit.CartItemRetrofitService

class CartItemRemoteSource(private val cartItemService: CartItemRetrofitService) :
    CartItemDataSource {
    override fun save(productId: Long, user: User): Result<Long> {
        val response =
            cartItemService.postCartItem("Basic ${user.token}", ProductIdEntity(productId))
                .execute()
        return response.runCatching {
            if (code() != 201) throw Throwable(message())
            headers()["Location"]?.substringAfterLast("/")?.toLong() ?: throw Throwable(message())
        }
    }

    override fun findAll(userToken: String): Result<List<CartItemEntity>> {
        val response = cartItemService.selectCartItems("Basic $userToken").execute()
        return response.runCatching {
            if (code() != 200) throw Throwable(message())
            body() ?: throw Throwable(message())
        }
    }

    override fun findAll(limit: Int, offset: Int, userToken: String): Result<List<CartItemEntity>> {
        val response = cartItemService.selectCartItems("Basic $userToken").execute()
        return response.runCatching {
            if (code() != 200) throw Throwable(message())
            val body = body() ?: throw Throwable(message())
            body.slice(offset until body.size).take(limit)
        }
    }

    override fun updateCountById(id: Long, count: Int, userToken: String): Result<Unit> {
        val response =
            cartItemService.updateCountCartItem("Basic $userToken", id, QuantityEntity(count))
                .execute()
        return response.runCatching {
            if (code() != 200) throw Throwable(message())
        }
    }

    override fun deleteById(id: Long, userToken: String): Result<Unit> {
        val response = cartItemService.deleteCartItem("Basic $userToken", id).execute()
        return response.runCatching {
            if (code() != 204) throw Throwable(message())
        }
    }
}
