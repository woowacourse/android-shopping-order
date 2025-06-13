package woowacourse.shopping.data.coupons.repository

import woowacourse.shopping.data.coupons.CouponItem
import woowacourse.shopping.data.coupons.CouponRequest
import woowacourse.shopping.domain.model.Coupon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.domain.model.DiscountType
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource
) : OrderRepository {

    override suspend fun fetchCoupons(): List<Coupon> = withContext(Dispatchers.IO) {
        val response: CouponRequest = orderRemoteDataSource.fetchCoupons()
        response.map { it.toDomainModel() }
    }

    override suspend fun addOrder(cartItemIds: List<Int>) = withContext(Dispatchers.IO) {
        orderRemoteDataSource.postOrder(cartItemIds)
    }
}
private fun CouponItem.toDomainModel(): Coupon {
    return Coupon(
        id = id,
        code = code,
        description = description,
        expirationDate = expirationDate.toLocalDate(),
        discountType = discountType.toDomainType(),
        discount = discount,
        minimumAmount = minimumAmount,
        buyQuantity = buyQuantity,
        getQuantity = getQuantity,
        availableStartTime = availableTime?.start?.toLocalTime(),
        availableEndTime = availableTime?.end?.toLocalTime()
    )
}

private fun String.toDomainType(): DiscountType {
    return when(this) {
        "fixed" -> DiscountType.FIXED
        "buyXgetY" -> DiscountType.BUY_X_GET_Y
        "freeShipping" -> DiscountType.FREE_SHIPPING
        "percentage" -> DiscountType.PERCENTAGE
        else -> throw IllegalArgumentException()
    }
}

private fun String.toLocalDate(): LocalDate {
    return LocalDate.parse(this, DateTimeFormatter.ISO_DATE)
}

private fun String.toLocalTime(): LocalTime {
    return LocalTime.parse(this)
}
