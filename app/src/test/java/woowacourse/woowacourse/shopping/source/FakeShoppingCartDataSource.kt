package woowacourse.shopping.source

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.model.CartItemDto
import woowacourse.shopping.remote.model.ProductDto

class FakeShoppingCartDataSource(
    private var cartItemDtos: List<CartItemDto> = listOf(),
) : ShoppingCartDataSource {

    constructor(vararg cartItemDtos: CartItemDto) : this(cartItemDtos.toList())

    override fun findByProductId(productId: Long): ProductIdsCountData {
        val foundItem = cartItemDtos.find { cartItemDto -> cartItemDto.product.id == productId }
            ?: throw NoSuchElementException("there is no product $productId")

        return ProductIdsCountData(foundItem.id, foundItem.quantity)
    }

    override fun loadAllCartItems(): List<CartItemDto> = cartItemDtos

    override fun addNewProduct(productIdsCount: ProductIdsCount) {
        val newId = cartItemDtos.size.toLong() + 1
        val newCartItem = CartItemDto(
            newId, productIdsCount.quantity, ProductDto(
                productIdsCount.productId, "1", 1, "1", "unit"
            )
        )
        cartItemDtos = cartItemDtos + newCartItem
    }

    override fun removeCartItem(cartItemId: Long) {
        val foundItem = cartItemDtos.find { cartItemDto -> cartItemDto.id == cartItemId }
            ?: throw NoSuchElementException()
        cartItemDtos = cartItemDtos - foundItem
    }

    override fun plusProductsIdCount(cartItemId: Long, quantity: Int) {
        TODO("Not yet implemented")
    }

    override fun minusProductsIdCount(cartItemId: Long, quantity: Int) {
        TODO("Not yet implemented")
    }

    override fun updateProductsCount(cartItemId: Long, newQuantity: Int) {
        val foundItem = cartItemDtos.find { cartItemDto -> cartItemDto.id == cartItemId }
            ?: throw NoSuchElementException()
        val updatedItem = foundItem.copy(quantity = newQuantity)
        val newCartItems = cartItemDtos.map { cartItemDto ->
            if (cartItemDto.id == cartItemId) updatedItem else cartItemDto
        }
        cartItemDtos = newCartItems
    }
}
