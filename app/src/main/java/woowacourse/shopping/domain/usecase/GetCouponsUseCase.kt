package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Coupons
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CouponRepository
import java.time.LocalDate

class GetCouponsUseCase(
    private val repository: CouponRepository,
) {
    suspend operator fun invoke(
        products: Products,
        nowDate: LocalDate = LocalDate.now(),
    ): Result<Coupons> {
        val coupons =
            repository.fetchAllCoupons().getOrElse {
                return Result.failure(Throwable("[GetCouponsUseCase] 쿠폰 목록 불러오기 오류", it))
            }

        val validCoupons = coupons.filter { it.getIsAvailable(products, nowDate) }

        return Result.success(Coupons(validCoupons))
    }
}
