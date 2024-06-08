package woowacourse.shopping

/*
import com.example.domain.model.CartItem
import com.example.domain.model.Product
import com.example.domain.model.Quantity
import com.example.domain.repository.CartRepository
import woowacourse.shopping.presentation.products.uimodel.ProductUiModel

val imageUrl = "https://www.naver.com/"
val name = "올리브"
val price = 1500
val category = "food"

fun product(id: Int) =
    com.example.domain.model.Product(
        id = id,
        imageUrl = imageUrl,
        name = name,
        price = price,
        category = category,
    )

fun products(size: Int): List<com.example.domain.model.Product> {
    return List(size) { product(it) }
}

fun cartItem(
    id: Int,
    quantity: com.example.domain.model.Quantity = com.example.domain.model.Quantity(),
): com.example.domain.model.CartItem {
    return com.example.domain.model.CartItem(id, id, quantity)
}

fun cartItems(size: Int): List<com.example.domain.model.CartItem> {
    return List(size) { cartItem(it) }
}

fun convertProductUiModel(
    cartItems: List<com.example.domain.model.CartItem>,
    products: List<com.example.domain.model.Product>,
): List<ProductUiModel> {
    return products.map { product ->
        val cartItem = cartItems.firstOrNull { it.productId == product.id }
        if (cartItem == null) {
            ProductUiModel.from(product)
        } else {
            ProductUiModel.from(product, cartItem.quantity)
        }
    }
}

fun convertProductUiModel(
    productEntities: List<com.example.domain.model.Product>,
    cartRepository: com.example.domain.repository.CartRepository,
): List<ProductUiModel> {
    return productEntities.map { product ->
        runCatching { cartRepository.find(product.id) }
            .map { ProductUiModel.from(product, it.quantity) }
            .getOrElse { ProductUiModel.from(product) }
    }
}
*/
