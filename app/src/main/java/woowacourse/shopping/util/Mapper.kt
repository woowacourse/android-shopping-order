package woowacourse.shopping.util

import woowacourse.shopping.data.remote.cart.CartResponse
import woowacourse.shopping.data.remote.cart.CartResponse.Content.CartRemoteProduct
import woowacourse.shopping.data.remote.coupon.CouponResponse
import woowacourse.shopping.data.remote.product.ProductResponse
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Product
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun CartProduct.updateQuantity(newQuantity: Int): CartProduct = this.copy(quantity = newQuantity)

fun CartRemoteProduct.toDomain(): Product =
    Product(
        id = id,
        name = name,
        price = price,
        imageUrl = imageUrl,
        category = category,
    )

fun ProductResponse.Content.toDomain(): Product =
    Product(
        id = id,
        name = name,
        price = price,
        imageUrl = imageUrl,
        category = category,
    )

fun CartResponse.Content.toDomain(): CartProduct =
    CartProduct(
        id = id,
        product = product.toDomain(),
        quantity = quantity,
    )

fun CouponResponse.toDomain(): Coupon {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

    val formattedDate =
        this.expirationDate?.let {
            LocalDate.parse(it).format(dateFormatter)
        } ?: ""

    return Coupon(
        id = this.id,
        code = this.code,
        description = this.description,
        expirationDate = formattedDate,
        discountType = this.discountType,
        discount = this.discount ?: 0,
        minimumAmount = this.minimumAmount ?: 0,
        buyQuantity = this.buyQuantity ?: 0,
        getQuantity = this.getQuantity ?: 0,
        availableTime =
            this.availableTime?.start?.let {
                LocalTime.parse(it, timeFormatter)
            } ?: LocalTime.MIN,
    )
}
