package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.remote.cart.CartItemsRemoteDataSource
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class CartItemsRepositoryImpl(
    private val cartItemsRemoteDataSource: CartItemsRemoteDataSource,
) : CartItemRepository {
    override fun getAllCartItemSize(callback: (Int) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getAllCartItem(callback: (List<ProductUiModel>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun subListCartItems(
        startIndex: Int,
        endIndex: Int,
        callback: (List<ProductUiModel>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun insertCartItem(product: ProductUiModel, onComplete: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateCartItem(product: ProductUiModel, onComplete: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteCartItemById(productId: Long, onComplete: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateOrInsertItem(product: ProductUiModel, callback: () -> Unit) {
        TODO("Not yet implemented")
    }

}
