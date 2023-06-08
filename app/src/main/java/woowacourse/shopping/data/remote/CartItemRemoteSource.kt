package woowacourse.shopping.data.remote

import woowacourse.shopping.data.datasource.CartItemDataSource
import woowacourse.shopping.data.entity.CartItemEntity
import woowacourse.shopping.data.entity.ProductIdEntity
import woowacourse.shopping.data.entity.QuantityEntity
import woowacourse.shopping.data.remote.retrofit.CartItemRetrofitService
import woowacourse.shopping.error.DataError

class CartItemRemoteSource(private val cartItemService: CartItemRetrofitService) :
    CartItemDataSource {
    override fun save(productId: Long, userToken: String): Result<Long> {
        return runCatching {
            val response = cartItemService.postCartItem(
                Authorization.KEY_FORMAT.format(userToken), ProductIdEntity(productId)
            ).execute()
            if (response.code() != 201) throw DataError.CartSaveError(
                response.errorBody()?.string()
            )
            response.headers()["Location"]?.substringAfterLast("/")?.toLong()
                ?: throw DataError.CartSaveError(
                    response.errorBody()?.string()
                )
        }
    }

    override fun findAll(userToken: String): Result<List<CartItemEntity>> {
        return runCatching {
            val response =
                cartItemService.selectCartItems(Authorization.KEY_FORMAT.format(userToken))
                    .execute()
            if (response.code() != 200) throw DataError.CartFindError(
                response.errorBody()?.string()
            )
            response.body() ?: throw DataError.CartFindError(response.errorBody()?.string())
        }
    }

    override fun findAll(limit: Int, offset: Int, userToken: String): Result<List<CartItemEntity>> {
        return runCatching {
            val response =
                cartItemService.selectCartItems(Authorization.KEY_FORMAT.format(userToken))
                    .execute()
            if (response.code() != 200) throw DataError.CartFindError(
                response.errorBody()?.string()
            )
            val body =
                response.body() ?: throw DataError.CartFindError(response.errorBody()?.string())
            body.slice(offset until body.size).take(limit)
        }
    }

    override fun updateCountById(id: Long, count: Int, userToken: String): Result<Unit> {
        return runCatching {
            val response = cartItemService.updateCountCartItem(
                Authorization.KEY_FORMAT.format(userToken), id, QuantityEntity(count)
            ).execute()
            if (response.code() != 200) throw DataError.CartUpdateError(
                response.errorBody()?.string()
            )
        }
    }

    override fun deleteById(id: Long, userToken: String): Result<Unit> {
        return runCatching {
            val response =
                cartItemService.deleteCartItem(Authorization.KEY_FORMAT.format(userToken), id)
                    .execute()
            if (response.code() != 204) throw DataError.CartDeleteError(
                response.errorBody()?.string()
            )
        }
    }
}
