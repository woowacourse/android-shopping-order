package woowacourse.shopping.data.cart

import woowacourse.shopping.data.cart.remote.RemoteCartDataSource
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val remoteCartDataSource: RemoteCartDataSource = RemoteCartDataSource(),
) : CartRepository {
    override suspend fun loadAll(): Result<List<Cart>> {
        return runCatching {
//            val maxCount = remoteCartDataSource.getCount().quantity // 1번 방법
            var maxCount = 0
            getCount() // 2번 방법
                .onSuccess { maxCount = it }
                .onFailure { maxCount = MAXIMUM_CART_ITEM_COUNT }
            return load(
                0,
                maxCount,
            )
        }
    }

    override suspend fun getCount(): Result<Int> {
        return runCatching {
            remoteCartDataSource.getCount().quantity
        }
    }

    override suspend fun load(
        startPage: Int,
        pageSize: Int,
    ): Result<List<Cart>> {
        return runCatching {
            remoteCartDataSource.load(startPage, pageSize).cartDto.map { it.toCart() }
        }
    }

    override suspend fun loadById(productId: Long): Result<Cart> {
        return runCatching {
            var foundCart: Cart? = null
            loadAll().onSuccess { carts ->
                foundCart = carts.firstOrNull { it.product.id == productId }
            }
            foundCart ?: throw NoSuchElementException(EXCEPTION_NO_SUCH_PRODUCT)
        }
    }

    override suspend fun deleteExistCartItem(productId: Long): Result<Unit> {
        return runCatching {
            loadById(productId).onSuccess { cart ->
                remoteCartDataSource.delete(cart.cartId)
            }
        }
    }

    override suspend fun applyDeltaToCartQuantity(
        productId: Long,
        quantityDelta: Int,
    ): Result<Int> {
        return runCatching {
            var resultQuantity = quantityDelta
            loadById(productId).onSuccess { targetCart ->
                resultQuantity = targetCart.quantity + quantityDelta
                handleCartItemByQuantity(targetCart, resultQuantity)
            }.onFailure { e ->
                if (e.message == EXCEPTION_NO_SUCH_PRODUCT) {
                    saveNewCartItem(
                        productId,
                        resultQuantity,
                    )
                }
            }
            resultQuantity
        }
    }

    override suspend fun setNewCartQuantity(
        productId: Long,
        newQuantity: Int,
    ): Result<Unit> {
        return runCatching {
            loadById(productId).onSuccess { targetCart ->
                handleCartItemByQuantity(targetCart, newQuantity)
            }.onFailure { e ->
                if (e.message == EXCEPTION_NO_SUCH_PRODUCT) {
                    saveNewCartItem(
                        productId,
                        newQuantity,
                    )
                }
            }
        }
    }

    private suspend fun handleCartItemByQuantity(
        targetCart: Cart,
        quantity: Int,
    ) {
        when {
            (0 < quantity) ->
                updateExistCartItem(
                    targetCart.cartId,
                    quantity,
                )

            (0 == quantity) -> deleteExistCartItem(targetCart.product.id)

            else -> throw IllegalArgumentException("newQuantity는 0보다 작을 수 없습니다")
        }
    }

    private suspend fun saveNewCartItem(
        productId: Long,
        quantity: Int,
    ): Result<Unit> {
        return runCatching {
            remoteCartDataSource.save(productId, quantity)
        }
    }

    private suspend fun updateExistCartItem(
        cartId: Long,
        resultQuantity: Int,
    ): Result<Unit> {
        return runCatching {
            remoteCartDataSource.update(cartId, resultQuantity)
        }
    }

    companion object {
        private const val EXCEPTION_NO_SUCH_PRODUCT = "상품이 장바구니에 존재하지 않습니다."
        private const val MAXIMUM_CART_ITEM_COUNT = 999
    }
}
