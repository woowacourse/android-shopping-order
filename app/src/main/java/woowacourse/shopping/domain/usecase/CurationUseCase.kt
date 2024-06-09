package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.CartItemRepository
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.ProductRepository
import woowacourse.shopping.domain.RecentProductRepository

class CurationUseCase(
    private val recentProductRepository: RecentProductRepository,
    private val productRepository: ProductRepository,
    private val cartItemRepository: CartItemRepository
) {

    suspend operator fun invoke(count: Int): Result<List<CartProduct>> = runCatching {
        recentProductRepository.findOrNull().getOrNull()?.let {
            val products = productRepository.getProducts(it.category, 0, count).getOrThrow()
            val cartItems = cartItemRepository.getCartItems(0, MAXIMUM_CART_SIZE).getOrThrow()

            val cartProductIds = cartItems.map { it.productId }.toSet()

            val filteredProducts = products.filter { product -> product.productId !in cartProductIds }

            val cartProducts =
                filteredProducts.map { product ->
                    val cart = cartItems.find { it.productId == product.productId }

                    CartProduct(
                        productId = product.productId,
                        name = product.name,
                        imgUrl = product.imgUrl,
                        price = product.price,
                        category = product.category,
                        cartId = cart?.cartId?: 0,
                        quantity = cart?.quantity ?: 0,
                    )
                }
            return Result.success(cartProducts)
        }
        return Result.failure(Throwable())
    }

    companion object {
        const val MAXIMUM_CART_SIZE = 1000
    }
}

