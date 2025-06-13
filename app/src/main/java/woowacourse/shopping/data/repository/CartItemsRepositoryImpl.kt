package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.CachedCartItem
import woowacourse.shopping.data.model.Content
import woowacourse.shopping.data.source.local.cart.CartItemsLocalDataSource
import woowacourse.shopping.data.source.remote.cart.CartItemsRemoteDataSource
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.repository.CartItemsRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class CartItemsRepositoryImpl(
    private val cartItemsRemoteDataSource: CartItemsRemoteDataSource,
    private val cartItemsLocalDataSource: CartItemsLocalDataSource,
) : CartItemsRepository {
    override suspend fun preloadCartItems(): Result<Unit> {
        return cartItemsRemoteDataSource.getCartItems(null, null)
            .mapCatching { remoteItems ->
                val cachedCartItems =
                    remoteItems.content.map {
                        CachedCartItem(
                            cartId = it.id,
                            productId = it.product.id,
                            quantity = it.quantity,
                        )
                    }
                cartItemsLocalDataSource.saveCartItems(cachedCartItems)
            }
    }

    override suspend fun getCartItems(
        page: Int?,
        size: Int?,
    ): Result<PagingData> {
        return cartItemsRemoteDataSource.getCartItems(page, size)
            .mapCatching { response ->
                PagingData(
                    products = response.content.map { it.toUiModel() },
                    hasNext = !response.last,
                    hasPrevious = !response.first,
                )
            }
    }

    override suspend fun deleteCartItem(id: Long): Result<Unit> {
        val cartId =
            cartItemsLocalDataSource.getCartId(id)
                ?: return Result.failure(IllegalArgumentException(DELETE_ERROR))

        return cartItemsRemoteDataSource.deleteCartItem(cartId)
            .mapCatching { cartItemsLocalDataSource.remove(id) }
    }

    override suspend fun addCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit> {
        return cartItemsRemoteDataSource.addCartItem(id, quantity)
            .mapCatching { cartId -> cartItemsLocalDataSource.add(cartId, id, quantity) }
    }

    override suspend fun updateCartItemQuantity(
        id: Long,
        quantity: Int,
    ): Result<Unit> {
        val cartId =
            cartItemsLocalDataSource.getCartId(id)
                ?: return Result.failure(IllegalArgumentException(DELETE_ERROR))

        return cartItemsRemoteDataSource.updateCartItem(cartId, quantity)
            .mapCatching { cartItemsLocalDataSource.update(id, quantity) }
    }

    override suspend fun addCartItemQuantity(
        id: Long,
        quantity: Int,
    ): Result<Unit> {
        val cartId = cartItemsLocalDataSource.getCartId(id)
        val currentQuantity = cartItemsLocalDataSource.getQuantity(id)
        val updatedQuantity = currentQuantity + quantity

        return if (cartId != null) {
            cartItemsRemoteDataSource.updateCartItem(cartId, updatedQuantity)
                .mapCatching {
                    cartItemsLocalDataSource.update(id, updatedQuantity)
                }
        } else {
            cartItemsRemoteDataSource.addCartItem(id, quantity)
                .mapCatching { newCartId ->
                    cartItemsLocalDataSource.add(newCartId, id, quantity)
                }
        }
    }

    override suspend fun getCartItemsCount(): Result<Int> {
        return cartItemsRemoteDataSource.getCartCount()
            .mapCatching { response -> response.quantity }
    }

    override suspend fun getQuantity(pagingData: PagingData): Result<PagingData> {
        preloadCartItems()
        val updatedProducts =
            pagingData.products.map { product ->
                val quantity = cartItemsLocalDataSource.getQuantity(product.id)
                val isExpanded = quantity > 0
                product.copy(quantity = quantity, isExpanded = isExpanded)
            }
        return Result.success(pagingData.copy(products = updatedProducts))
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
        private const val DELETE_ERROR = "장바구니에 상품이 존재하지 않습니다."
    }
}
