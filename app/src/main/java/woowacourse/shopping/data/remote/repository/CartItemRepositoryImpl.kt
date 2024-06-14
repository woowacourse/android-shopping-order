package woowacourse.shopping.data.remote.repository

import woowacourse.shopping.data.remote.datasource.cartItem.CartItemDataSource
import woowacourse.shopping.data.remote.datasource.cartItem.RetrofitCartItemDataSource
import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.repository.CartItemRepository

class CartItemRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource = RetrofitCartItemDataSource(),
) : CartItemRepository {
    override suspend fun getAllByPaging(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> {
        return cartItemDataSource.getAllByPaging(page, size).mapCatching { it ->
            it.body?.content
                ?.map { it.toDomain() }
                ?: emptyList()
        }
    }

    override suspend fun post(cartItemRequest: CartItemRequest): Result<Int> {
        return cartItemDataSource.post(cartItemRequest).mapCatching {
            it.body ?: 0
        }
    }

    override suspend fun patch(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Result<Unit> {
        return cartItemDataSource.patch(id, quantityRequestDto).mapCatching {
            it.body
        }
    }

    override suspend fun delete(id: Int): Result<Unit> {
        return cartItemDataSource.delete(id).mapCatching {
            it.body
        }
    }

    override suspend fun getCount(): Result<Int> {
        return cartItemDataSource.getCount().mapCatching {
            it.body?.quantity ?: 0
        }
    }
}
