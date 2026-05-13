package woowacourse.shopping.data.repository

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
    private suspend fun getCartContents(): List<CartContent> = remoteDataSource.getCartItems(0, 20)

    override suspend fun getCart(): Cart {
        val cartContents = getCartContents()

        val items =
            cartContents.map {
                CartItem(
                    Product(
                        id = it.product.id,
                        name =
                            ProductName(
                                it.product.name,
                            ),
                        price =
                            Money(
                                it.product.price.toLong(),
                            ),
                        imageUrl = it.product.imageUrl,
                    ),
                    it.quantity,
                )
            }

        return Cart(items)
    }

    override suspend fun addItem(
        id: Long,
        quantity: Int,
    ) {
        remoteDataSource.addItem(
            id = id,
            quantity = quantity,
        )
    }

    override suspend fun deleteItem(productId: Long): RemoveItemResult {
        val cartContent = getCartContents().find { it.product.id == productId } ?: return RemoveItemResult.NotFoundItem
        remoteDataSource.deleteItem(cartContent.id)
        return RemoveItemResult.Success(getCart())
    }

    override suspend fun changeCartItem(
        productId: Long,
        amount: Int,
    ): Cart {
        val cartContent = getCartContents().find { it.product.id == productId }
        if (cartContent != null) {
            remoteDataSource.changeQuantity(cartContent.id, amount)
        }
        return getCart()
    }
}
