package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.Content
import woowacourse.shopping.data.source.remote.cart.CartItemsRemoteDataSource
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class CartItemsRepositoryImpl(
    private val cartItemsRemoteDataSource: CartItemsRemoteDataSource,
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
                        hasNext = !response.last
                    )
                }
                .let(onResult)
        }
    }

    override fun addCartItem(
        id: Int,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartItemsRemoteDataSource.addCartItem(id, quantity) { result ->
            result
                .mapCatching { it }
                .let(onResult)
        }
    }

    override fun deleteCartItem(
        id: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartItemsRemoteDataSource.deleteCartItem(id) { result ->
            result
                .mapCatching { it }
                .let(onResult)
        }
    }

    override fun updateCartItem(
        id: Int,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartItemsRemoteDataSource.updateCartItem(id, quantity) { result ->
            result
                .mapCatching { it }
                .let(onResult)
        }
    }

    override fun getCarItemsCount(
        onResult: (Result<Int>) -> Unit,
    ) {
        cartItemsRemoteDataSource.getCarItemsCount() { result ->
            result
                .mapCatching { it.numberOfElements }
                .let(onResult)
        }
    }


    private fun Content.toUiModel() =
        ProductUiModel(
            id = this.product.id,
            imageUrl = this.product.imageUrl,
            name = this.product.name,
            quantity = this.quantity,
            price = this.product.price
        )
}
