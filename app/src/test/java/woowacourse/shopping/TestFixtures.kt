package woowacourse.shopping

import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.policy.DiscountPolicy
import woowacourse.shopping.domain.model.coupon.policy.FreeShippingDiscountPolicy
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.ui.cart.CartUiModel
import woowacourse.shopping.ui.cart.CartUiModels
import woowacourse.shopping.ui.coupon.CouponUiModel
import woowacourse.shopping.ui.products.adapter.recent.RecentProductUiModel
import woowacourse.shopping.ui.products.adapter.type.ProductUiModel
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.LocalTime

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

fun cartItemsBySize(size: Int) = List(size) { cartItem(it, 1) }

fun cartItemsByTotalPrice(totalPrice: Int): List<CartItem> {
    return listOf(
        CartItem(
            id = 0,
            product = Product(0, "", totalPrice, "", ""),
            quantity = Quantity(1),
        ),
    )
}

fun cartItemsByProductQuantity(vararg productQuantities: Int): List<CartItem> {
    val cartItems = mutableListOf<CartItem>()
    productQuantities.forEachIndexed { index, quantity ->
        cartItems.add(CartItem(index, product(id = index), Quantity(quantity)))
    }
    return cartItems
}

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

fun List<Coupon>.toCouponUiModels(): List<CouponUiModel> {
    return map { CouponUiModel.from(it) }
}

fun availableTime(
    startHour: Int,
    endHour: Int,
): AvailableTime {
    val start = LocalTime.of(startHour, 0)
    val end = LocalTime.of(endHour, 0)
    return AvailableTime(start, end)
}

fun coupon(
    id: Int,
    discountPolicy: DiscountPolicy = FreeShippingDiscountPolicy(listOf()),
) = Coupon(id, "", LocalDate.MAX, 0, discountPolicy)

fun coupons(size: Int): List<Coupon> = List(size) { coupon(it) }
