package woowacourse.shopping.data.repository

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
    override fun getCartItems(
        page: Int,
        size: Int,
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
        id: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartItemsRemoteDataSource.deleteCartItem(id) { result ->
            cartItemsLocalDataSource.remove(id)
            result.let(onResult)
        }
    }

    override fun upsertCartItem(
        id: Int,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        if (cartItemsLocalDataSource.isCached(id)) {
            cartItemsRemoteDataSource.updateCartItem(id, quantity) { result ->
                cartItemsLocalDataSource.update(id, quantity)
                result.let(onResult)
            }
        } else {
            cartItemsRemoteDataSource.addCartItem(id, quantity) { result ->
                cartItemsLocalDataSource.add(id, quantity)
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
