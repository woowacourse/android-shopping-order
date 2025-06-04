package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository
import java.time.LocalDate
import java.time.LocalTime

class GetAvailableCouponUseCase(
    private val couponRepository: CouponRepository,
) {
    suspend operator fun invoke(): Result<List<Coupon>> =
        couponRepository
            .fetchAll()
            .mapCatching { coupons ->
                val today = LocalDate.now()
                val now = LocalTime.now()
                coupons.filter { coupon ->
                    val notExpired = coupon.expirationDate >= today
                    val inAvailableTime =
                        coupon.availableTime?.let {
                            now >= it.start && now <= it.end
                        } ?: true

                    notExpired && inAvailableTime
                }
            }.onFailure { throwable -> throw throwable }
}
