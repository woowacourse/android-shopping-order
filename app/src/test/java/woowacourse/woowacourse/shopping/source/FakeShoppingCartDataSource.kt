package woowacourse.shopping.source

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.model.response.CartItemResponse
import woowacourse.shopping.remote.model.response.ProductResponse

class FakeShoppingCartDataSource(
    private var cartItemResponses: List<CartItemResponse> = listOf(),
) : ShoppingCartDataSource {
    constructor(vararg cartItemResponses: CartItemResponse) : this(cartItemResponses.toList())

    override fun findByProductId(productId: Long): ProductIdsCountData {
        val foundItem =
            cartItemResponses.find { cartItemDto -> cartItemDto.product.id == productId }
                ?: throw NoSuchElementException("there is no product $productId")

        return ProductIdsCountData(foundItem.id, foundItem.quantity)
    }

    override fun loadAllCartItems(): List<CartItemResponse> = cartItemResponses

    override fun addNewProduct(productIdsCount: ProductIdsCount) {
        val newId = cartItemResponses.size.toLong() + 1
        val newCartItem =
            CartItemResponse(
                newId,
                productIdsCount.quantity,
                ProductResponse(
                    productIdsCount.productId,
                    "1",
                    1,
                    "1",
                    "unit",
                ),
            )
        cartItemResponses = cartItemResponses + newCartItem
    }

    override fun removeCartItem(cartItemId: Long) {
        val foundItem =
            cartItemResponses.find { cartItemDto -> cartItemDto.id == cartItemId }
                ?: throw NoSuchElementException()
        cartItemResponses = cartItemResponses - foundItem
    }

    override fun plusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun minusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    ) {
        TODO("Not yet implemented")
    }

    override fun updateProductsCount(
        cartItemId: Long,
        newQuantity: Int,
    ) {
        val foundItem =
            cartItemResponses.find { cartItemDto -> cartItemDto.id == cartItemId }
                ?: throw NoSuchElementException()
        val updatedItem = foundItem.copy(quantity = newQuantity)
        val newCartItems =
            cartItemResponses.map { cartItemDto ->
                if (cartItemDto.id == cartItemId) updatedItem else cartItemDto
            }
        cartItemResponses = newCartItems
    }
}
