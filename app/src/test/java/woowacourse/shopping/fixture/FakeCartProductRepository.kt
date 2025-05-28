package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartProductRepository

class FakeCartProductRepository : CartProductRepository {
    private val cartProducts = mutableListOf<CartProduct>()

    override fun insert(
        productId: Int,
        quantity: Int,
        onSuccess: (Int) -> Unit,
    ) {
        val product =
            Product(
                id = productId,
                imageUrl = "",
                name = "Product $productId",
                price = 1000,
            )
        cartProducts.add(CartProduct(product = product, quantity = quantity))
    }

    override fun getPagedProducts(
        page: Int?,
        size: Int?,
        onSuccess: (PagedResult<CartProduct>) -> Unit,
    ) {
        if (page == null || size == null) {
            onSuccess(PagedResult(cartProducts, false))
            return
        }
        val pagedItems = cartProducts.drop(page * size).take(size)
        val hasNext = page * size + pagedItems.size < cartProducts.size
        onSuccess(PagedResult(pagedItems, hasNext))
    }

    override fun getCartProductByProductId(
        productId: Int,
        onSuccess: (CartProduct?) -> Unit,
    ) {
        onSuccess(cartProducts.find { it.product.id == productId })
    }

    override fun getTotalQuantity(onSuccess: (Int) -> Unit) {
        onSuccess(cartProducts.sumOf { it.quantity })
    }

    override fun updateQuantity(
        productId: Int,
        quantityToAdd: Int,
        onSuccess: () -> Unit,
    ) {
        getCartProductByProductId(productId) { cartProduct ->
            val newQuantity = cartProduct?.quantity?.plus(quantityToAdd) ?: 0
            when {
                cartProduct == null -> insert(productId, newQuantity) { onSuccess() }
                newQuantity == 0 -> delete(cartProduct.id) { onSuccess() }
                else -> {
                    cartProducts.replaceAll {
                        if (it.product.id == productId) it.copy(quantity = newQuantity) else it
                    }
                    onSuccess()
                }
            }
        }
    }

    override fun delete(
        id: Int,
        onSuccess: () -> Unit,
    ) {
        cartProducts.removeIf { it.product.id == id }
        onSuccess()
    }
}
