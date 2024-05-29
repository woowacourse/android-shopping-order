package woowacourse.shopping.remote

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.ShoppingCartProductIdDataSource
import woowacourse.shopping.domain.model.ProductIdsCount

class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) :
    ShoppingCartProductIdDataSource {
    override fun findByProductId(productId: Long): ProductIdsCountData? {
        return null
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

    override fun loadAll(): List<ProductIdsCountData> {
        return emptyList()
    }

    override fun isFinalPage(page: Int): Boolean =
        cartItemApiService.requestCartItems(page - 1).execute().body()?.last
            ?: throw IllegalArgumentException()

    override fun addedNewProductsId(productIdsCount: ProductIdsCount): Long {
        val call = cartItemApiService.addCartItem(
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
    override fun removedProductsId(productId: Long): Long {
        val cartItem = cartItemApiService.requestCartItems().execute().body()?.content?.find {
            it.product.id == productId
        } ?: throw NoSuchElementException()
        cartItemApiService.removeCartItem(cartItem.id)
        return 10
    }

    override fun plusProductsIdCount(
        productId: Long,
        quantity: Int,
    ) {
        val cartItem = cartItemApiService.requestCartItems().execute().body()?.content?.find {
            it.product.id == productId
        } ?: throw NoSuchElementException()
        cartItemApiService.updateCartItemQuantity(cartItem.id, quantity).execute()
    }

    override fun minusProductsIdCount(
        productId: Long,
        quantity: Int,
    ) {
        val body = cartItemApiService.requestCartItems().execute().body()
        val cartItem =
            body?.content?.find { it.product.id == productId } ?: throw NoSuchElementException()
        cartItemApiService.updateCartItemQuantity(cartItem.id, quantity).execute()
    }

    override fun clearAll() {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "CartItemRemoteDataSource"
    }
}
