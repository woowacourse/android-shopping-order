package woowacourse.shopping.mapper

import com.example.domain.model.Coupon
import woowacourse.shopping.data.remote.response.OrderResponseDto

fun OrderResponseDto.toDomain(): Coupon =
    Coupon(
        id = this.id,
        name = this.name,
    )
