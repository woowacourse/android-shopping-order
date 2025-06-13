package woowacourse.shopping.data.carts.repository

import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.dto.CartResponse
import woowacourse.shopping.domain.model.Goods

class CartRepositoryImpl(
    private val remoteDataSource: CartRemoteDataSource,
) : CartRepository {

    override suspend fun checkValidBasicKey(validKey: String): Int {
        return try {
            remoteDataSource.fetchAuthCode(validKey)
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }

    override suspend fun fetchAllCartItems(): CartResponse {
        return try {
            remoteDataSource.fetchCartItemByOffset(Int.MAX_VALUE, 0)
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }

    override suspend fun fetchCartItemsByOffset(limit: Int, offset: Int): CartResponse {
        return try {
            remoteDataSource.fetchCartItemByOffset(limit, offset)
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }

    override suspend fun fetchCartItemsByPage(page: Int, size: Int): CartResponse {
        return try {
            remoteDataSource.fetchCartItemByPage(page, size)
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }

    override suspend fun updateQuantity(cartId: Int, cartQuantity: CartQuantity) {
        try {
            remoteDataSource.updateCartItemCount(cartId, cartQuantity)
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }

    override suspend fun delete(cartId: Int): Int {
        return try {
            remoteDataSource.deleteItem(cartId)
            cartId
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }

    override suspend fun getAllItemsSize(): Int {
        return try {
            remoteDataSource.fetchCartCount()
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }

    override suspend fun addCartItem(goods: Goods, quantity: Int): Int {
        return try {
            remoteDataSource.addItem(goods.id, quantity)
            goods.id
        } catch (e: Exception) {
            throw CartFetchError.Network
        }
    }
}
