package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartProductRepository

class FakeCartProductRepository : CartProductRepository {
    private val cartProducts = mutableListOf<CartProduct>()
    private var nextCartId = 1

    override suspend fun insert(
        productId: Int,
        quantity: Int,
    ): Result<Int> =
        try {
            val product =
                Product(
                    id = productId,
                    imageUrl = "",
                    name = "Product $productId",
                    price = 1000,
                )
            val cartProduct =
                CartProduct(
                    id = nextCartId++,
                    product = product,
                    quantity = quantity,
                )
            cartProducts.add(cartProduct)
            Result.success(cartProduct.id) // cartId 반환
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<CartProduct>> =
        try {
            if (page == null || size == null) {
                Result.success(PagedResult(cartProducts, hasNext = false))
            } else {
                val pagedItems = cartProducts.drop(page * size).take(size)
                val hasNext = (page + 1) * size < cartProducts.size
                Result.success(PagedResult(pagedItems, hasNext))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getCartProductByProductId(productId: Int): Result<CartProduct?> =
        try {
            val product = cartProducts.find { it.product.id == productId }
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getTotalQuantity(): Result<Int> =
        try {
            Result.success(cartProducts.sumOf { it.quantity })
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun updateQuantity(
        cartProduct: CartProduct,
        quantityToAdd: Int,
    ): Result<Unit> =
        try {
            val newQuantity = cartProduct.quantity + quantityToAdd
            if (newQuantity == 0) {
                delete(cartProduct.id)
            } else {
                cartProducts.replaceAll {
                    if (it.product.id == cartProduct.product.id) {
                        it.copy(quantity = newQuantity)
                    } else {
                        it
                    }
                }
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun delete(id: Int): Result<Unit> =
        try {
            cartProducts.removeIf { it.product.id == id }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
