package woowacourse.shopping.domain

import kotlinx.coroutines.flow.firstOrNull
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.domain.model.Product

suspend fun recommendProductUseCase(
    productRepository: ProductRepository,
    cartRepository: CartRepository,
): List<Product> {
    val cart = cartRepository.cart
    val recentProducts =
        productRepository.getRecentProductsStream(1).firstOrNull() ?: return emptyList()
    val recentProduct = recentProducts.firstOrNull() ?: return emptyList()
    val sameCategoryProducts = productRepository.products.value.filter { it.category == recentProduct.category }
    return sameCategoryProducts
        .filter { product ->
            product.id !in cart.value.items.map { it.product.id }
        }.take(10)
}
