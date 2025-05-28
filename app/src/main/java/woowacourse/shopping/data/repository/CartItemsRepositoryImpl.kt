package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.CachedCartItem
import woowacourse.shopping.data.model.Content
import woowacourse.shopping.data.source.local.cart.CartItemsLocalDataSource
import woowacourse.shopping.data.source.remote.cart.CartItemsRemoteDataSource
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class CartItemsRepositoryImpl(
    private val cartItemsRemoteDataSource: CartItemsRemoteDataSource,
    private val cartItemsLocalDataSource: CartItemsLocalDataSource,
) : CartItemRepository {

    init {
        getInitialCartItems(null, null) { result ->
            result
                .onSuccess { it ->
                    cartItemsLocalDataSource.getCachedCartItem(it)
                }
        }
    }

    override fun getQuantity(pagingData: PagingData): PagingData {
        val updatedProducts = pagingData.products.map { product ->
            val quantity = cartItemsLocalDataSource.getQuantity(product.id)
            val isExpanded = quantity > 0
            product.copy(quantity = quantity, isExpanded = isExpanded)
        }
        return pagingData.copy(products = updatedProducts)
    }

    override fun getInitialCartItems(
        page: Int?,
        size: Int?,
        onResult: (Result<List<CachedCartItem>>) -> Unit,
    ) {
        cartItemsRemoteDataSource.getCartItems(page, size) { result ->
            result
                .mapCatching { response ->
                    response.content.map { content ->
                        CachedCartItem(
                            productId = content.product.id,
                            cartId = content.id,
                            quantity = content.quantity
                        )
                    }
                }
                .let(onResult)
        }
    }

    override fun getCartItems(
        page: Int?,
        size: Int?,
        onResult: (Result<PagingData>) -> Unit,
    ) {
        cartItemsRemoteDataSource.getCartItems(page, size) { result ->
            result
                .mapCatching { response ->
                    PagingData(
                        products = response.content.map { it.toUiModel() },
                        hasNext = !response.last,
                        hasPrevious = !response.first,
                    )
                }
                .let(onResult)
        }
    }

    override fun deleteCartItem(
        id: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartItemsRemoteDataSource.deleteCartItem(id) { result ->
            cartItemsLocalDataSource.remove(id)
            result.let(onResult)
        }
    }

    override fun addCartItem(
        id: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartItemsRemoteDataSource.addCartItem(id, quantity) { result ->
            result
                .mapCatching { cartId ->
                    cartItemsLocalDataSource.add(cartId, id, quantity)
                }
                .let(onResult)
        }
    }

    override fun updateCartItem(
        id: Long,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val cartId = cartItemsLocalDataSource.findCachedCartId(id)
        if (cartId != null) {
            cartItemsRemoteDataSource.updateCartItem(cartId, quantity) { result ->
                result.let(onResult)
            }
        }
    }

    override fun getCartItemsCount(onResult: (Result<Int>) -> Unit) {
        cartItemsRemoteDataSource.getCarItemsCount { result ->
            result
                .mapCatching {
                    it.quantity
                }
                .let(onResult)
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
}
