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
        cartProduct: CartProduct,
        quantityToAdd: Int,
        onSuccess: () -> Unit,
    ) {
        val newQuantity = cartProduct.quantity + quantityToAdd
        when {
            newQuantity == 0 -> delete(cartProduct.id) { onSuccess() }
            else -> {
                cartProducts.replaceAll {
                    if (it.product.id == cartProduct.product.id) it.copy(quantity = newQuantity) else it
                }
                onSuccess()
            }
        }
        return
    }

    override fun delete(
        id: Int,
        onSuccess: () -> Unit,
    ) {
        cartProducts.removeIf { it.product.id == id }
        onSuccess()
    }
}
