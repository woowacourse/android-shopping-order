package woowacourse.shopping.data.cart

import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.Quantity

class CartRepositoryImpl(private val cartRemoteDataSource: CartRemoteDataSource) : CartRepository {
    override fun getCartItem(productId: Long): Result<CartWithProduct> =
        getAllCartItemsWithProduct().mapCatching {
            it.firstOrNull { it.product.id == productId } ?: error("장바구니 정보를 불러올 수 없습니다.")
        }

    override fun getAllCartItems(): Result<List<Cart>> {
        val size = getCartItemCounts()
        return cartRemoteDataSource.getCartItems(0, size.getOrThrow()).mapCatching {
            it.content.map {
                Cart(id = it.id, productId = it.product.id, quantity = Quantity(it.quantity))
            }
        }
    }

    override fun getAllCartItemsWithProduct(): Result<List<CartWithProduct>> {
        val size = getCartItemCounts()
        return cartRemoteDataSource.getCartItems(0, size.getOrThrow()).mapCatching {
            it.content.map {
                CartWithProduct(
                    it.id,
                    it.product.toDomain(),
                    Quantity(it.quantity),
                )
            }
        }
    }

    override fun postCartItems(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        cartRemoteDataSource.postCartItems(
            RequestCartItemPostDto(
                productId = productId,
                quantity = quantity,
            ),
        )

    override fun deleteCartItem(id: Long): Result<Unit> = cartRemoteDataSource.deleteCartItems(id)

    override fun getCartItemCounts(): Result<Int> =
        cartRemoteDataSource.getCartItemCounts().mapCatching {
            it.quantity
        }

    override fun patchCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit> = cartRemoteDataSource.patchCartItems(id = id, request = RequestCartItemsPatchDto(quantity))

    override fun addProductToCart(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            val cart: Cart? = getAllCartItems().getOrThrow().firstOrNull { it.productId == productId }
            if (cart == null) {
                postCartItems(productId, quantity)
                return@runCatching
            }
            patchCartItem(cart.id, cart.quantity.value + quantity)
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
