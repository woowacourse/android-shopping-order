package woowacourse.shopping.mapper

import com.example.domain.model.Coupon
import com.example.domain.model.TotalPrice
import woowacourse.shopping.data.remote.response.AppliedTotalResponseDto
import woowacourse.shopping.data.remote.response.CouponsResponseDto

fun CouponsResponseDto.toDomain(): Coupon =
    Coupon(
        id = this.id,
        name = this.name,
    )

fun AppliedTotalResponseDto.toDomain(): TotalPrice =
    TotalPrice(
        discountPrice = this.discountPrice,
        finalPrice = this.totalPrice,
    )

// fun OrderCompleteResponseDto.toDomain(): Receipt =
//    Receipt(
//        orderProducts = this.orderProducts.map { it.toDomain }
//    )
//
