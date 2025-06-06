package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.CachedCartItem
import woowacourse.shopping.data.model.CartItemResponse.Content
import woowacourse.shopping.data.source.local.cart.CartItemsLocalDataSource
import woowacourse.shopping.data.source.remote.cart.CartItemsRemoteDataSource
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class CartItemsRepositoryImpl(
    private val cartItemsRemoteDataSource: CartItemsRemoteDataSource,
    private val cartItemsLocalDataSource: CartItemsLocalDataSource,
) : CartItemRepository {
//    init {
//        getInitialCartItems(null, null) {
//                .onSuccess { cachedCartItems ->
//                    cartItemsLocalDataSource.getCachedCartItem(cachedCartItems)
//                }
//        }
//    }

    override fun getQuantity(pagingData: PagingData): PagingData {
        val updatedProducts =
            pagingData.products.map { product ->
                val quantity = cartItemsLocalDataSource.getQuantity(product.id)
                product.copy(quantity = quantity)
            }
        return pagingData.copy(products = updatedProducts)
    }

    override fun getCartItemProductIds(): List<Long> {
        return cartItemsLocalDataSource.getCartItemProductIds()
    }

    override fun getCartItemCartIds(): List<Long> {
        return cartItemsLocalDataSource.getCartItemCartIds()
    }

    override suspend fun getInitialCartItems(
        page: Int?,
        size: Int?,
    ): Result<List<CachedCartItem>> {
        val result = cartItemsRemoteDataSource.getCartItems(page, size)

        return result.mapCatching { response ->
            response.content.map { content ->
                CachedCartItem(
                    productId = content.product.id,
                    cartId = content.id,
                    quantity = content.quantity,
                )
            }
        }
    }

    override suspend fun getCartItems(
        page: Int?,
        size: Int?,
    ): Result<PagingData> {
        val result = cartItemsRemoteDataSource.getCartItems(page, size)

        return result.mapCatching { response ->
            PagingData(
                products = response.content.map { it.toUiModel() },
                page = response.pageable.pageNumber,
                hasNext = !response.last,
                hasPrevious = !response.first,
            )
        }
    }

    override suspend fun deleteCartItem(id: Long): Result<Unit> {
        val cartId = cartItemsLocalDataSource.findCachedCartId(id)

        return if (cartId != null) {
            val result = cartItemsRemoteDataSource.deleteCartItem(cartId)

            result.onSuccess {
                cartItemsLocalDataSource.remove(cartId)
            }

            result
        } else {
            Result.failure(Exception(CART_ID_ERROR_MESSAGE))
        }
    }

    override suspend fun addCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit> {
        return cartItemsRemoteDataSource.addCartItem(id, quantity)
            .mapCatching { cartId ->
                cartItemsLocalDataSource.add(cartId, id, quantity)
            }
    }

    override suspend fun updateCartItemQuantity(
        id: Long,
        quantity: Int,
    ): Result<Unit> {
        val cartId = cartItemsLocalDataSource.findCachedCartId(id)

        return if (cartId != null) {
            val result = cartItemsRemoteDataSource.updateCartItem(cartId, quantity)

            result.onSuccess {
                cartItemsLocalDataSource.update(id, quantity)
            }

            result
        } else {
            Result.failure(Exception(CART_ID_ERROR_MESSAGE))
        }
    }

    override suspend fun addCartItemQuantity(
        id: Long,
        quantity: Int,
    ): Result<Unit> {
        val cartId = cartItemsLocalDataSource.findCachedCartId(id)
        val updatedQuantity = cartItemsLocalDataSource.getQuantity(id) + quantity

        return if (cartId != null) {
            val result = cartItemsRemoteDataSource.updateCartItem(cartId, updatedQuantity)

            result.onSuccess {
                cartItemsLocalDataSource.update(id, updatedQuantity)
            }

            result
        } else {
            cartItemsRemoteDataSource.addCartItem(id, quantity)
                .mapCatching { newCartId ->
                    cartItemsLocalDataSource.add(newCartId, id, quantity)
                }
        }
    }

    override suspend fun getCartItemsCount(): Result<Int> {
        return cartItemsRemoteDataSource.getCarItemsCount()
            .mapCatching {
                it.quantity
            }
    }

    private fun Content.toUiModel() =
        ProductUiModel(
            id = this.product.id,
            imageUrl = this.product.imageUrl,
            name = this.product.name,
            quantity = this.quantity,
            price = this.product.price,
        )

    companion object {
        private const val CART_ID_ERROR_MESSAGE = "[ERROR] 해당 productId에 대한 cartId를 찾을 수 없습니다."
    }
}
