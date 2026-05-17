package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import woowacourse.shopping.data.source.remote.CartRemoteDataSource
import woowacourse.shopping.data.source.remote.dto.cart.CartContent
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Money
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductName
import woowacourse.shopping.domain.model.RemoveItemResult
import woowacourse.shopping.domain.repository.CartRepository

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
        val allCartContents = mutableListOf<CartContent>()
        while (true) {
            val pagedCartContents = remoteDataSource.getCartItems(page, 5)
            allCartContents.addAll(pagedCartContents)
            if (pagedCartContents.size < 5) break
            page += 1
        }
        _cart.update {
            Cart(
                allCartContents.map { cartContent ->
                    CartItem(
                        id = cartContent.id,
                        product =
                            Product(
                                id = cartContent.product.id,
                                name = ProductName(cartContent.product.name),
                                price = Money(cartContent.product.price.toLong()),
                                imageUrl = cartContent.product.imageUrl,
                                category = cartContent.product.category,
                            ),
                        quantity = cartContent.quantity,
                    )
                },
            )
        }
    }
}
