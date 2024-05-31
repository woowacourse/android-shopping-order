package woowacourse.shopping.data.source.cart

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.cart.CartItemApiService
import woowacourse.shopping.remote.cart.CartItemDto
import woowacourse.shopping.remote.cart.CartItemRequest

class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) :
    CartItemDataSource {
    override fun findByProductId(productId: Long): ProductIdsCountData? {
        val allCartItems =
            cartItemApiService.requestCartItems().execute().body()?.content
                ?: throw IllegalStateException()
        val find = allCartItems.find { it.product.id == productId } ?: return null

        return ProductIdsCountData(find.product.id, find.quantity)
    }

    override fun loadPaged(page: Int): List<ProductIdsCountData> {
        val response =
            cartItemApiService.requestCartItems(page = page - 1).execute().body()?.content
                ?: throw NoSuchElementException("there is no product with page: $page")

        return response.map {
            ProductIdsCountData(
                productId = it.product.id,
                quantity = it.quantity,
            )
        }.also {
        }
    }

    override fun loadPagedItems(page: Int): List<CartItemDto> {
        val response =
            cartItemApiService.requestCartItems(page = page - 1).execute().body()?.content
                ?: throw NoSuchElementException("there is no product with page: $page")

        return response.map {
            CartItemDto(
                id = it.id,
                quantity = it.quantity,
                product = it.product,
            )
        }
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

    override fun loadAll(): List<ProductIdsCountData> {
        return emptyList()
    }

    override fun isFinalPage(page: Int): Boolean =
        cartItemApiService.requestCartItems(page - 1).execute().body()?.last
            ?: throw IllegalArgumentException()

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

    override fun clearAll() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "CartItemRemoteDataSource"
    }
}
