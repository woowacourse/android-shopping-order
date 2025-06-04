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
        onResult: (Result<Int>) -> Unit,
    ) {
        val product =
            Product(
                id = productId,
                imageUrl = "",
                name = "Product $productId",
                price = 1000,
            )
        cartProducts.add(CartProduct(id = product.id, product = product, quantity = quantity))
        onResult(Result.success(product.id))
    }

    override fun getPagedProducts(
        page: Int?,
        size: Int?,
        onResult: (Result<PagedResult<CartProduct>>) -> Unit,
    ) {
        if (page == null || size == null) {
            onResult(Result.success(PagedResult(cartProducts, false)))
            return
        }
        val pagedItems = cartProducts.drop(page * size).take(size)
        val hasNext = page * size + pagedItems.size < cartProducts.size
        onResult(Result.success(PagedResult(pagedItems, hasNext)))
    }

    override fun getCartProductByProductId(
        productId: Int,
        onResult: (Result<CartProduct?>) -> Unit,
    ) {
        onResult(Result.success(cartProducts.find { it.product.id == productId }))
    }

    override fun getTotalQuantity(onResult: (Result<Int>) -> Unit) {
        onResult(Result.success(cartProducts.sumOf { it.quantity }))
    }

    override fun updateQuantity(
        cartProduct: CartProduct,
        newQuantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        when {
            newQuantity == 0 -> delete(cartProduct.id) { onResult(Result.success(Unit)) }
            else -> {
                cartProducts.replaceAll {
                    if (it.product.id == cartProduct.product.id) it.copy(quantity = newQuantity) else it
                }
                onResult(Result.success(Unit))
            }
        }
        return
    }

    override fun delete(
        id: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartProducts.removeIf { it.product.id == id }
        onResult(Result.success(Unit))
    }

    override fun deleteAll(
        ids: Set<Int>,
        onResult: (Result<Unit>) -> Unit,
    ) {
        cartProducts.removeIf { it.product.id in ids }
        onResult(Result.success(Unit))
    }
}
