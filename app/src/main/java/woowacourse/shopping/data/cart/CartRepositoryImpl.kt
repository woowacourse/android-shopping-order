package woowacourse.shopping.data.cart

import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.datasource.OrderRemoteDataSource
import woowacourse.shopping.data.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.dto.request.RequestOrdersPostDto
import woowacourse.shopping.data.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.exception.ShoppingError
import woowacourse.shopping.exception.ShoppingException
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.Quantity

class CartRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource,
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : CartRepository {
    override suspend fun getCartItemByProductId(productId: Long): Result<CartWithProduct> =
        getAllCartItemsWithProduct().mapCatching { cartWithProducts ->
            cartWithProducts.firstOrNull { cartWithProduct -> cartWithProduct.product.id == productId }
                ?: throw ShoppingException(ShoppingError.CartNotFound)
        }

    override suspend fun getCartItemByCartId(cartId: Long): Result<CartWithProduct> =
        getAllCartItemsWithProduct().mapCatching { cartWithProducts ->
            cartWithProducts.firstOrNull { cartWithProduct -> cartWithProduct.id == cartId }
                ?: throw ShoppingException(ShoppingError.CartNotFound)
        }

    override suspend fun getAllCartItems(): Result<List<Cart>> {
        val size = getCartItemCounts()
        return cartRemoteDataSource.getCartItems(0, size.getOrThrow()).mapCatching { dto ->
            dto.content.map { content ->
                Cart(
                    id = content.id,
                    productId = content.product.id,
                    quantity = Quantity(content.quantity),
                )
            }
        }
    }

    override suspend fun getAllCartItemsWithProduct(): Result<List<CartWithProduct>> {
        val size = getCartItemCounts()
        return cartRemoteDataSource.getCartItems(0, size.getOrThrow()).mapCatching { dto ->
            dto.content.map { content ->
                CartWithProduct(
                    content.id,
                    content.product.toDomain(),
                    Quantity(content.quantity),
                )
            }
        }
    }

    override suspend fun postCartItems(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        cartRemoteDataSource.postCartItems(
            RequestCartItemPostDto(
                productId = productId,
                quantity = quantity,
            ),
        )

    override suspend fun deleteCartItem(id: Long): Result<Unit> = cartRemoteDataSource.deleteCartItems(id)

    override suspend fun getCartItemCounts(): Result<Int> =
        cartRemoteDataSource.getCartItemCounts().mapCatching {
            it.quantity
        }

    override suspend fun patchCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit> {
        return cartRemoteDataSource.patchCartItems(
            id = id,
            request = RequestCartItemsPatchDto(quantity),
        )
    }

    override suspend fun addProductToCart(
        productId: Long,
        quantity: Int,
    ): Result<Unit> {
        val cart: Cart? =
            getAllCartItems().getOrThrow().firstOrNull { it.productId == productId }
        if (cart == null) {
            return postCartItems(productId, quantity)
        }
        return patchCartItem(cart.id, cart.quantity.value + quantity)
    }

    override suspend fun order(cartItemIds: List<Long>) {
        orderRemoteDataSource.order(RequestOrdersPostDto(cartItemIds = cartItemIds))
    }

    private fun ResponseCartItemsGetDto.Product.toDomain() =
        Product(
            id = this.id,
            imageUrl = this.imageUrl,
            name = this.name,
            price = this.price,
            category = this.category,
        )
}
