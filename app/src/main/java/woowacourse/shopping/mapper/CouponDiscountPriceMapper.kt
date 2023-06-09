package woowacourse.shopping.mapper

import com.example.domain.model.CouponDiscountPrice
import woowacourse.shopping.data.remote.request.CouponDiscountPriceDTO
import woowacourse.shopping.model.CouponDiscountPriceUIModel

fun CouponDiscountPrice.toUIModel(): CouponDiscountPriceDTO {
    return CouponDiscountPriceDTO(
        discountPrice = this.discountPrice,
        totalPrice = this.totalPrice,
    )
}

fun CouponDiscountPriceDTO.toDomain(): CouponDiscountPrice {
    return CouponDiscountPrice(
        discountPrice = this.discountPrice,
        totalPrice = this.totalPrice,
    )
}

fun CouponDiscountPriceUIModel.toDomain(): CouponDiscountPrice {
    return CouponDiscountPrice(
        discountPrice = this.discountPrice,
        totalPrice = this.totalPrice,
    )
}
