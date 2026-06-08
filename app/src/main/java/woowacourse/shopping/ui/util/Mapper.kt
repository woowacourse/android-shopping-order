package woowacourse.shopping.ui.util

import woowacourse.shopping.constant.Format.formatPrice
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.cart.CartItems
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.Coupons
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.Products
import woowacourse.shopping.ui.cart.CartItemUiModel
import woowacourse.shopping.ui.order.CouponUiModel
import woowacourse.shopping.ui.order.DiscountConditionUiModel
import woowacourse.shopping.ui.productList.ProductUiModel
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd")

fun List<CartItem>.toUiModel(selectedItems: Set<Int>): List<CartItemUiModel> =
    this.map { cartItem ->
        val isSelected = selectedItems.contains(cartItem.id)
        cartItem.toUiModel(isSelected)
    }

fun CartItem.toUiModel(isSelected: Boolean): CartItemUiModel =
    CartItemUiModel(
        id = id,
        product = product,
        quantity = quantity.value,
        isSelected = isSelected,
        totalPrice = totalPrice,
    )

fun Products.toUiModel(cartItems: CartItems): List<ProductUiModel> = this.items.toUiModel(cartItems)

fun List<Product>.toUiModel(cartItems: CartItems): List<ProductUiModel> =
    this.map { product ->
        product.toUiModel(cartItems.getQuantityByProductId(product.id).value)
    }

fun Product.toUiModel(cartAmount: Int): ProductUiModel =
    ProductUiModel(
        id = this.id,
        name = this.name.value,
        price = formatPrice(this.price.value),
        imageUrl = this.imageUrl.value,
        cartAmount = cartAmount.toString(),
        showAmountController = cartAmount > 0,
    )

fun Coupons.toUiModel(selectedCoupon: Coupon? = null): List<CouponUiModel> =
    this.coupons.map { coupon ->
        CouponUiModel(
            id = coupon.id,
            description = coupon.description,
            expiredDate = coupon.expirationDate.format(DATE_FORMATTER),
            condition = coupon.toConditions(),
            isSelected = coupon == selectedCoupon,
        )
    }

private fun Coupon.toConditions(): DiscountConditionUiModel? =
    when (this) {
        is Coupon.FixedCoupon -> {
            val condition = if (minimumAmount == 0) "없음" else formatPrice(minimumAmount)
            DiscountConditionUiModel("최소 주문 금액", condition)
        }

        is Coupon.PercentageCoupon -> {
            DiscountConditionUiModel("사용 가능 시간", "${availableTime.start} ~ ${availableTime.end}")
        }

        is Coupon.BuyXGetYCoupon -> null

        is Coupon.FreeShipping -> {
            val condition = if (minimumAmount == 0) "없음" else formatPrice(minimumAmount)
            DiscountConditionUiModel("최소 주문 금액", condition)
        }
    }
