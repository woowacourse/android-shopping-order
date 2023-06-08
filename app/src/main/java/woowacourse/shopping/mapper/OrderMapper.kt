package woowacourse.shopping.mapper

import com.example.domain.model.Coupon
import com.example.domain.model.OrderProduct
import com.example.domain.model.Receipt
import com.example.domain.model.TotalPrice
import woowacourse.shopping.data.remote.response.AppliedTotalResponseDto
import woowacourse.shopping.data.remote.response.CouponsResponseDto
import woowacourse.shopping.data.remote.response.OrderCompleteResponseDto

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

fun OrderCompleteResponseDto.toDomain(): Receipt =
    Receipt(
        id = this.id,
        orderProducts = this.orderProductDto.map { it.toDomain() },
        originPrice = this.originPrice,
        couponName = this.couponName,
        totalPrice = this.totalPrice,
    )

fun OrderCompleteResponseDto.OrderProductDto.toDomain(): OrderProduct =
    OrderProduct(
        product = this.productResponse.toDomain(),
        quantity = this.quantity,
    )
