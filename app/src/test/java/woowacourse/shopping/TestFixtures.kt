package woowacourse.shopping

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.model.RecentProduct
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

fun cartItem(
    id: Int,
    quantity: Quantity = Quantity(),
): CartItem {
    return CartItem(id, id, quantity)
}

fun cartItems(size: Int): List<CartItem> {
    return List(size) { cartItem(it) }
}
//
// fun convertProductUiModel(
//    cartItems: List<CartItem>,
//    products: List<Product>,
// ): List<ProductUiModel> {
//    return products.map { product ->
//        val cartItem = cartItems.firstOrNull { it.productId == product.id }
//        if (cartItem == null) {
//            ProductUiModel.from(product)
//        } else {
//            ProductUiModel.from(product, cartItem.quantity)
//        }
//    }
// }
//
// fun convertProductUiModel(
//    productEntities: List<Product>,
//    cartRepository: CartRepository,
// ): List<ProductUiModel> {
//    return productEntities.map { product ->
//        runCatching { cartRepository.find(product.id) }
//            .map { ProductUiModel.from(product, it.quantity) }
//            .getOrElse { ProductUiModel.from(product) }
//    }
// }
