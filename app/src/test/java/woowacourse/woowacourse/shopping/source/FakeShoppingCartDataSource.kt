package woowacourse.shopping.source

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.model.CartItemData
import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.remote.model.response.CartItemResponse
import woowacourse.shopping.remote.model.response.ProductResponse
import woowacourse.shopping.remote.model.response.toData

class FakeShoppingCartDataSource(
    private var cartItemResponses: List<CartItemResponse> = listOf(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
) : ShoppingCartDataSource {
    constructor(vararg cartItemResponses: CartItemResponse) : this(cartItemResponses.toList())

    override suspend fun findByProductId(productId: Long): Result<ProductIdsCountData> = runCatchingWithDispatcher {
        val foundItem =
            cartItemResponses.find { cartItemResponse -> cartItemResponse.product.id == productId }
                ?: throw NoSuchElementException("there is no product $productId")

        ProductIdsCountData(foundItem.id, foundItem.quantity)
    }

    override suspend fun findCartItemByProductId(productId: Long): Result<CartItemData> = runCatchingWithDispatcher {
        val foundItem = cartItemResponses.find { cartItemResponse -> cartItemResponse.product.id == productId }
            ?: throw NoSuchElementException("there is no product $productId")

        foundItem.toData()
    }

    override suspend fun loadAllCartItems(): Result<List<CartItemData>> = runCatchingWithDispatcher {
        cartItemResponses.toData()
    }

    override suspend fun addNewProduct(productIdsCountData: ProductIdsCountData): Result<Unit> =
        runCatchingWithDispatcher {
            val newCartItemResponse =
                CartItemResponse(
                    id = productIdsCountData.productId,
                    quantity = productIdsCountData.quantity,
                    product = ProductResponse(
                        id = productIdsCountData.productId,
                        name = "name",
                        price = 1000,
                        imageUrl = "url",
                        category = "category",
                    ),
                )
            cartItemResponses = cartItemResponses + newCartItemResponse
        }

    override suspend fun removeCartItem(cartItemId: Long): Result<Unit> = runCatchingWithDispatcher {
        val foundItem =
            cartItemResponses.find { cartItemResponse -> cartItemResponse.id == cartItemId }
                ?: throw NoSuchElementException()
        cartItemResponses = cartItemResponses - foundItem
    }

    override suspend fun updateProductsCount(
        cartItemId: Long,
        newQuantity: Int,
    ): Result<Unit> = runCatchingWithDispatcher {
        val foundItem =
            cartItemResponses.find { cartItemResponse -> cartItemResponse.id == cartItemId }
                ?: throw NoSuchElementException()

        val updatedItem = foundItem.copy(quantity = newQuantity)
        val newCartItems =
            cartItemResponses.map { cartItemResponse ->
                if (cartItemResponse.id == cartItemId) updatedItem else cartItemResponse
            }
        cartItemResponses = newCartItems
    }

    private suspend fun <T> runCatchingWithDispatcher(
        dispatcher: CoroutineDispatcher = this.dispatcher,
        block: suspend () -> T
    ): Result<T> = runCatching {
        withContext(dispatcher) {
            block()
        }
    }

}
