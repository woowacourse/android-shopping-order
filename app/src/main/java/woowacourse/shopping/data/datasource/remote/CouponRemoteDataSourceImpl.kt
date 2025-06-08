package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.dto.coupon.toDomain
import woowacourse.shopping.data.service.CouponService
import woowacourse.shopping.domain.model.Coupon

class CouponRemoteDataSourceImpl(
    private val couponService: CouponService,
) : CouponRemoteDataSource {
    override suspend fun fetchAll(): Result<List<Coupon>> =
        handleApiCall(
            errorMessage = "쿠폰 목록 조회 실패",
            apiCall = { couponService.requestCoupons() },
            transform = { response ->
                response.body()?.map { it.toDomain() }
                    ?: throw IllegalStateException("응답 바디가 null입니다.")
            },
        )
}
