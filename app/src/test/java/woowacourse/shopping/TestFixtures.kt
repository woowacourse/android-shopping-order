package woowacourse.shopping

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.FixedCoupon
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.ui.cart.CartUiModel
import woowacourse.shopping.ui.cart.CartUiModels
import woowacourse.shopping.ui.coupon.CouponUiModel
import woowacourse.shopping.ui.products.adapter.recent.RecentProductUiModel
import woowacourse.shopping.ui.products.adapter.type.ProductUiModel
import java.lang.IllegalArgumentException
import java.time.LocalDate

val imageUrl = "https://www.naver.com/"
val name = "올리브"
val price = 1500
val category = "food"

fun product(id: Int) = Product(id = id, imageUrl = imageUrl, name = name, price = price, category = category)

fun products(size: Int) = List(size) { product(it) }

fun recentProduct(productId: Int) = RecentProduct(0, product(productId))

fun recentProducts(size: Int) = List(size) { recentProduct(it) }

fun cartItem(
    id: Int,
    quantity: Int,
) = CartItem(id, product(id), Quantity(quantity))

fun cartItems(size: Int) = List(size) { cartItem(it, 1) }

suspend fun List<Product>.toProductUiModels(cartRepository: CartRepository): List<ProductUiModel> {
    return map { product ->
        val cartItem = cartRepository.findByProductId(product.id).getOrNull()
        if (cartItem == null) {
            ProductUiModel.from(product)
        } else {
            ProductUiModel.from(product, cartItem.quantity)
        }
    }
}

fun List<RecentProduct>.toRecentProductUiModels(): List<RecentProductUiModel> {
    return map { RecentProductUiModel(it.product.id, it.product.imageUrl, it.product.name) }
}

fun List<CartItem>.toCartUiModels(products: List<Product>): CartUiModels {
    val uiModels =
        map { cartItem ->
            val product = products.find { it.id == cartItem.product.id }
            product ?: throw IllegalArgumentException()
            CartUiModel.from(product, cartItem)
        }
    return CartUiModels(uiModels)
}

fun fixedCoupon(
    id: Int,
    expirationDate: LocalDate = LocalDate.of(3000, 10, 10),
    discount: Int,
    minimumPrice: Int = 0,
): FixedCoupon {
    return FixedCoupon(id, "", "", expirationDate, discount, minimumPrice)
}

fun List<Coupon>.toCouponUiModels(): List<CouponUiModel> {
    return map { CouponUiModel.from(it) }
}
