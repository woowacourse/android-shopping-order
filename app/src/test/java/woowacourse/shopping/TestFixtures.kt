package woowacourse.shopping

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.ui.products.adapter.recent.RecentProductUiModel
import woowacourse.shopping.ui.products.adapter.type.ProductUiModel
import java.time.LocalDateTime

val imageUrl = "https://www.naver.com/"
val name = "올리브"
val price = 1500
val category = "food"

fun product(id: Int) = Product(id = id, imageUrl = imageUrl, name = name, price = price, category = category)

fun products(size: Int): List<Product> {
    return List(size) { product(it) }
}

fun recentProduct(productId: Int) = RecentProduct(0, product(productId), LocalDateTime.now())

fun recentProducts(size: Int) = List(size) { recentProduct(it) }

fun cartItem(
    id: Int,
    quantity: Int,
): CartItem {
    return CartItem(id, id, Quantity(quantity))
}

fun List<Product>.toProductUiModels(cartItems: List<CartItem>): List<ProductUiModel> {
    return map { product ->
        val cartItem = cartItems.firstOrNull { it.productId == product.id }
        if (cartItem == null) {
            ProductUiModel.from(product)
        } else {
            ProductUiModel.from(product, cartItem.quantity)
        }
    }
}

fun List<Product>.toProductUiModels(cartRepository: CartRepository): List<ProductUiModel> {
    return map { product ->
        runCatching { cartRepository.syncFindByProductId(product.id) }
            .map {
                if (it == null) {
                    ProductUiModel.from(product)
                } else {
                    ProductUiModel.from(product, it.quantity)
                }
            }
            .getOrElse { ProductUiModel.from(product) }
    }
}

fun List<RecentProduct>.toRecentProductUiModels(): List<RecentProductUiModel> {
    return map { RecentProductUiModel(it.product.id, it.product.imageUrl, it.product.name) }
}
