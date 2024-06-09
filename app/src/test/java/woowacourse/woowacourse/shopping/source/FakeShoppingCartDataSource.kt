package woowacourse.shopping.source

import woowacourse.shopping.data.model.CartItemData
import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.remote.model.response.CartItemResponse
import woowacourse.shopping.remote.model.response.ProductResponse

class FakeShoppingCartDataSource(
    private var cartItemResponses: List<CartItemResponse> = listOf(),
) : ShoppingCartDataSource {
    constructor(vararg cartItemResponses: CartItemResponse) : this(cartItemResponses.toList())

    override suspend fun findByProductId2(productId: Long): Result<ProductIdsCountData> =
        runCatching {
            val foundItem =
                cartItemResponses.find { cartItemResponse -> cartItemResponse.product.id == productId }
                    ?: throw NoSuchElementException("there is no product $productId")

            ProductIdsCountData(foundItem.id, foundItem.quantity)
        }

    override suspend fun loadAllCartItems2(): Result<List<CartItemData>> =
        runCatching {
            cartItemResponses.map { cartItemResponse ->
                CartItemData(
                    id = cartItemResponse.id,
                    quantity = cartItemResponse.quantity,
                    product =
                        ProductData(
                            id = cartItemResponse.product.id,
                            name = cartItemResponse.product.name,
                            price = cartItemResponse.product.price,
                            imgUrl = cartItemResponse.product.imageUrl,
                        ),
                )
            }
        }

    override suspend fun addNewProduct2(productIdsCountData: ProductIdsCountData): Result<Unit> =
        runCatching {
            val newCartItemResponse =
                CartItemResponse(
                    id = productIdsCountData.productId,
                    quantity = productIdsCountData.quantity,
                    product =
                        ProductResponse(
                            id = productIdsCountData.productId,
                            name = "name",
                            price = 1000,
                            imageUrl = "url",
                            category = "category",
                        ),
                )
            cartItemResponses = cartItemResponses + newCartItemResponse
        }

    override suspend fun removeCartItem2(cartItemId: Long): Result<Unit> =
        runCatching {
            val foundItem =
                cartItemResponses.find { cartItemResponse -> cartItemResponse.id == cartItemId }
                    ?: throw NoSuchElementException()
            cartItemResponses = cartItemResponses - foundItem
        }

    override suspend fun plusProductsIdCount2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            val foundItem =
                cartItemResponses.find { cartItemResponse -> cartItemResponse.id == cartItemId }
                    ?: throw NoSuchElementException()
            val updatedItem = foundItem.copy(quantity = foundItem.quantity + quantity)
            val newCartItems =
                cartItemResponses.map { cartItemResponse ->
                    if (cartItemResponse.id == cartItemId) updatedItem else cartItemResponse
                }
            cartItemResponses = newCartItems
        }

    override suspend fun minusProductsIdCount2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            val foundItem =
                cartItemResponses.find { cartItemResponse -> cartItemResponse.id == cartItemId }
                    ?: throw NoSuchElementException()
            val updatedItem = foundItem.copy(quantity = foundItem.quantity - quantity)
            val newCartItems =
                cartItemResponses.map { cartItemResponse ->
                    if (cartItemResponse.id == cartItemId) updatedItem else cartItemResponse
                }
            cartItemResponses = newCartItems
        }

    override suspend fun updateProductsCount2(
        cartItemId: Long,
        newQuantity: Int,
    ): Result<Unit> =
        runCatching {
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
}
