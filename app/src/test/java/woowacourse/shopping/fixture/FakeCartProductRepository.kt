package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartProductRepository

class FakeCartProductRepository : CartProductRepository {
    private val cartProducts = mutableListOf<CartProduct>()

    override suspend fun insert(
        productId: Int,
        quantity: Int,
    ): Result<Int> {
        val product =
            Product(
                id = productId,
                imageUrl = "",
                name = "Product $productId",
                price = 1000,
            )
        cartProducts.add(CartProduct(id = product.id, product = product, quantity = quantity))
        return Result.success(product.id)
    }

    override suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<CartProduct>> {
        if (page == null || size == null) {
            return Result.success(PagedResult(cartProducts, false))
        }
        val pagedItems = cartProducts.drop(page * size).take(size)
        val hasNext = page * size + pagedItems.size < cartProducts.size
        return Result.success(PagedResult(pagedItems, hasNext))
    }

    override suspend fun getCartProductByProductId(productId: Int): Result<CartProduct?> {
        return Result.success(cartProducts.find { it.product.id == productId })
    }

    override suspend fun getTotalQuantity(): Result<Int> {
        return Result.success(cartProducts.sumOf { it.quantity })
    }

    override suspend fun updateQuantity(
        cartProduct: CartProduct,
        newQuantity: Int,
    ): Result<Unit> {
        return when {
            newQuantity == 0 -> return delete(cartProduct.id)
            else -> {
                cartProducts.replaceAll {
                    if (it.product.id == cartProduct.product.id) it.copy(quantity = newQuantity) else it
                }
                Result.success(Unit)
            }
        }
    }

    override suspend fun delete(id: Int): Result<Unit> {
        cartProducts.removeIf { it.product.id == id }
        return Result.success(Unit)
    }

    override suspend fun deleteProductsByIds(ids: Set<Int>): Result<Unit> {
        cartProducts.removeIf { it.product.id in ids }
        return Result.success(Unit)
    }
}
