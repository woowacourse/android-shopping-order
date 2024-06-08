package woowacourse.shopping.remote.source

import woowacourse.shopping.data.model.CartItemData
import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.remote.model.request.CartItemRequest
import woowacourse.shopping.remote.model.response.toData
import woowacourse.shopping.remote.service.CartItemApiService

class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) :
    ShoppingCartDataSource {
    override fun findByProductId(productId: Long): ProductIdsCountData? {
        val allCartItems =
            cartItemApiService.requestCartItems().execute().body()?.content
                ?: throw IllegalStateException()
        val find = allCartItems.find { it.product.id == productId } ?: return null

        return ProductIdsCountData(find.product.id, find.quantity)
    }

    override fun loadAllCartItems(): List<CartItemData> {
        val response =
            cartItemApiService.requestCartItems().execute().body()?.content
                ?: throw NoSuchElementException("there is no product")

        return response.map {
            CartItemData(
                id = it.id,
                quantity = it.quantity,
                product = it.product.toData(),
            )
        }
    }

    override fun addNewProduct(productIdsCountData: ProductIdsCountData) {
        val call =
            cartItemApiService.addCartItem(
                CartItemRequest(
                    productIdsCountData.productId,
                    productIdsCountData.quantity,
                ),
            )
        call.execute()
    }

    override fun removeCartItem(cartItemId: Long) {
        cartItemApiService.removeCartItem(cartItemId).execute()
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

    override fun updateProductsCount(
        cartItemId: Long,
        newQuantity: Int,
    ) {
        cartItemApiService.updateCartItemQuantity(cartItemId, newQuantity).execute()
    }

    /*
    class ProductDataSourceImpl(private val productService: ProductService) : ProductDataSource {
    override fun findProductById(id: Long): Result<ProductDto> =
    override suspend fun findProductById(id: Long): Result<ProductDto> =
        runCatching {
            productService.getProductsById(id = id.toInt()).execute().body()?.toData()
                ?: throw IllegalArgumentException()
            productService.getProductsById(id = id.toInt()).toData()
        }

     */

    override suspend fun findByProductId2(productId: Long): Result<ProductIdsCountData> =
        runCatching {
            val allCartItems = cartItemApiService.requestCartItems2().content
            val find =
                allCartItems.find { it.product.id == productId }
                    ?: throw NoSuchElementException("there is no product with id $productId")
            ProductIdsCountData(find.product.id, find.quantity)
        }

    override suspend fun loadAllCartItems2(): Result<List<CartItemData>> =
        runCatching {
            val response = cartItemApiService.requestCartItems2().content
            response.map {
                CartItemData(
                    id = it.id,
                    quantity = it.quantity,
                    product = it.product.toData(),
                )
            }
        }

    override suspend fun addNewProduct2(productIdsCountData: ProductIdsCountData): Result<Unit> =
        runCatching {
            cartItemApiService.addCartItem2(
                CartItemRequest(
                    productIdsCountData.productId,
                    productIdsCountData.quantity,
                ),
            )
        }

    override suspend fun removeCartItem2(cartItemId: Long): Result<Unit> =
        runCatching {
            cartItemApiService.removeCartItem2(cartItemId)
        }

    override suspend fun plusProductsIdCount2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            cartItemApiService.updateCartItemQuantity2(cartItemId, quantity)
        }

    override suspend fun minusProductsIdCount2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            cartItemApiService.updateCartItemQuantity2(cartItemId, quantity)
        }

    override suspend fun updateProductsCount2(
        cartItemId: Long,
        newQuantity: Int,
    ): Result<Unit> =
        runCatching {
            cartItemApiService.updateCartItemQuantity2(cartItemId, newQuantity)
        }

    companion object {
        private const val TAG = "CartItemRemoteDataSource"
    }
}
