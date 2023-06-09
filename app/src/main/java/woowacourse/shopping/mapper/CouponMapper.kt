package woowacourse.shopping.mapper

import com.example.domain.model.Coupon
import woowacourse.shopping.data.remote.request.CouponDTO
import woowacourse.shopping.model.CouponUIModel

fun Coupon.toUIModel(): CouponUIModel {
    return CouponUIModel(
        id = this.id,
        name = this.name,
    )
}

fun CouponUIModel.toDomain(): Coupon {
    return Coupon(
        id = this.id,
        name = this.name,
    )
}

fun CouponDTO.toDomain(): Coupon {
    return Coupon(
        id = this.id,
        name = this.name,
    )
}
