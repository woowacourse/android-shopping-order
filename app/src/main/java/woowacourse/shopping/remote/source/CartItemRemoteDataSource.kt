package woowacourse.shopping.remote.source

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.service.CartItemApiService
import woowacourse.shopping.remote.model.CartItemRequest
import woowacourse.shopping.remote.model.CartItemDto

class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) :
    ShoppingCartDataSource {
    override fun findByProductId(productId: Long): ProductIdsCountData? {
        val allCartItems =
            cartItemApiService.requestCartItems().execute().body()?.content
                ?: throw IllegalStateException()
        val find = allCartItems.find { it.product.id == productId } ?: return null

        return ProductIdsCountData(find.product.id, find.quantity)
    }

    override fun loadAllCartItems(): List<CartItemDto> {
        val response =
            cartItemApiService.requestCartItems().execute().body()?.content
                ?: throw NoSuchElementException("there is no product")

        return response.map {
            CartItemDto(
                id = it.id,
                quantity = it.quantity,
                product = it.product,
            )
        }
    }

    override fun addedNewProductsId(productIdsCount: ProductIdsCount): Long {
        val call =
            cartItemApiService.addCartItem(
                CartItemRequest(
                    productIdsCount.productId,
                    productIdsCount.quantity,
                ),
            )
        call.execute()

        // TODO: code 값 확인해서 던지기
        return productIdsCount.productId
    }

    // TODO: 동작 안됨
    override fun removedProductsId(cartItemId: Long): Long {
        cartItemApiService.removeCartItem(cartItemId)
        return 10
    }

    override fun plusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    ) {
        cartItemApiService.updateCartItemQuantity(cartItemId, quantity).execute()
    }

    override fun minusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    ) {
        cartItemApiService.updateCartItemQuantity(cartItemId, quantity).execute()
    }

    companion object {
        private const val TAG = "CartItemRemoteDataSource"
    }
}
