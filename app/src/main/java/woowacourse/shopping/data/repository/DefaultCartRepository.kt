package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import woowacourse.shopping.data.source.remote.CartRemoteDataSource
import woowacourse.shopping.data.source.remote.dto.cart.response.CartContent
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Money
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductName
import woowacourse.shopping.domain.model.RemoveItemResult
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.error.Result

class DefaultCartRepository(
    private val remoteDataSource: CartRemoteDataSource,
) : CartRepository {
    private val _cart = MutableStateFlow(Cart())

    override val cart = _cart.asStateFlow()

    override suspend fun addItem(
        id: Long,
        quantity: Int,
    ) {
        remoteDataSource.addItem(
            id = id,
            quantity = quantity,
        )
        loadCart()
    }

    override suspend fun deleteItem(productId: Long): RemoveItemResult {
        val cartItem =
            _cart.value.items.find { it.product.id == productId }
                ?: return RemoveItemResult.NotFoundItem
        remoteDataSource.deleteItem(cartItem.id)
        loadCart()
        return RemoveItemResult.Success
    }

    override suspend fun changeCartItem(
        productId: Long,
        amount: Int,
    ) {
        val cartItem = _cart.value.items.find { it.product.id == productId }
        if (cartItem != null) {
            remoteDataSource.changeQuantity(cartItem.id, amount)
            loadCart()
        }
    }

    override suspend fun loadCart() {
        var page = 0
        val allCartContents = mutableSetOf<CartContent>()
        while (true) {
            val pagedCartContents = remoteDataSource.getCartItems(page, PAGE_SIZE)
            if (pagedCartContents is Result.Success) {
                allCartContents.addAll(pagedCartContents.data)
                if (pagedCartContents.data.size < PAGE_SIZE) break
                page += 1
                continue
            }
            break
        }
        _cart.update {
            Cart(
                allCartContents.map { cartContent ->
                    CartItem(
                        id = cartContent.id,
                        product =
                            Product(
                                id = cartContent.productContent.id,
                                name = ProductName(cartContent.productContent.name),
                                price = Money(cartContent.productContent.price.toLong()),
                                imageUrl = cartContent.productContent.imageUrl,
                                category = cartContent.productContent.category,
                            ),
                        quantity = cartContent.quantity,
                    )
                },
            )
        }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
