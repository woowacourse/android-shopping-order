package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.RemoteCartDataSource
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.remote.NetworkResult
import woowacourse.shopping.remote.datasource.RemoteCartDataSourceImpl

class CartRepositoryImpl(
    private val remoteCartDataSource: RemoteCartDataSource = RemoteCartDataSourceImpl(),
) : CartRepository {
    override suspend fun load(
        startPage: Int,
        pageSize: Int,
    ): Result<List<Cart>> {
        return runCatching {
            when (val result = remoteCartDataSource.getCartItems(startPage, pageSize)) {
                is NetworkResult.Success -> {
                    result.data.cartDto.map { it.toCart() }
                }
                is NetworkResult.Error -> {
                    throw result.exception
                }
            }
        }
    }

    override suspend fun saveNewCartItem(
        productId: Long,
        incrementAmount: Int,
    ): Result<Long> {
        return runCatching {
            when (val result = remoteCartDataSource.saveCartItem(productId, incrementAmount)) {
                is NetworkResult.Success -> {
                    result.data
                }
                is NetworkResult.Error -> {
                    throw result.exception
                }
            }
        }
    }

    override suspend fun updateCartItemQuantity(
        cartId: Long,
        newQuantity: Int,
    ): Result<Unit> {
        return if (newQuantity == 0) {
            deleteCartItem(cartId)
        } else {
            runCatching {
                val result = remoteCartDataSource.updateCartItemQuantity(cartId.toInt(), newQuantity)
                when (result) {
                    is NetworkResult.Success -> {
                        result.data
                    }
                    is NetworkResult.Error -> {
                        throw result.exception
                    }
                }
            }
        }
    }

    override suspend fun deleteCartItem(cartId: Long): Result<Unit> {
        return runCatching {
            when (val result = remoteCartDataSource.deleteCartItem(cartId.toInt())) {
                is NetworkResult.Success -> {
                    result.data
                }
                is NetworkResult.Error -> {
                    throw result.exception
                }
            }
        }
    }

    override suspend fun getCount(): Result<Int> {
        return runCatching {
            when (val result = remoteCartDataSource.getTotalCount()) {
                is NetworkResult.Success -> {
                    result.data.quantity
                }
                is NetworkResult.Error -> {
                    throw result.exception
                }
            }
        }
    }
}
