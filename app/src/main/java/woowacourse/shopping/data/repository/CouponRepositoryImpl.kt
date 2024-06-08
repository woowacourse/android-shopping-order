package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ApiHandleCouponDataSource
import woowacourse.shopping.data.datasource.impl.ApiHandleCouponDataSourceImpl
import woowacourse.shopping.data.remote.dto.response.BuyXGetYCoupon
import woowacourse.shopping.data.remote.dto.response.CouponDto
import woowacourse.shopping.data.remote.dto.response.FixedDiscountCoupon
import woowacourse.shopping.data.remote.dto.response.FreeShippingCoupon
import woowacourse.shopping.data.remote.dto.response.PercentageDiscountCoupon
import woowacourse.shopping.domain.model.coupon.BuyXFreeYCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedCoupon
import woowacourse.shopping.domain.model.coupon.PercentageCoupon
import woowacourse.shopping.domain.model.coupon.ShippingCoupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.response.Fail
import woowacourse.shopping.domain.response.Response
import woowacourse.shopping.domain.response.handleApiResult
import woowacourse.shopping.domain.response.result

class CouponRepositoryImpl(private val dataSource: ApiHandleCouponDataSource = ApiHandleCouponDataSourceImpl()) :
    CouponRepository {
    override suspend fun allCoupons(): List<Coupon> {
        val response = handleApiResult(
            result = dataSource.getCoupons(),
            transform = { it.map { it.toDomain() } }
        )
        return if (response is Fail.NotFound) emptyList() else response.result()
    }

    override suspend fun allCouponsResponse(): Response<List<Coupon>> = handleApiResult(
        result = dataSource.getCoupons(),
        transform = { it.map { it.toDomain() } }
    )


    private fun CouponDto.toDomain(): Coupon =
        when (this) {
            is BuyXGetYCoupon ->
                BuyXFreeYCoupon(
                    id,
                    code,
                    description,
                    expirationDate,
                    buyQuantity,
                    getQuantity,
                )

            is FixedDiscountCoupon ->
                FixedCoupon(
                    id,
                    description,
                    expirationDate,
                    discount,
                    minimumAmount,
                    code,
                )

            is FreeShippingCoupon ->
                ShippingCoupon(
                    id,
                    code,
                    description,
                    expirationDate,
                    minimumAmount,
                )

            is PercentageDiscountCoupon ->
                PercentageCoupon(
                    id,
                    code,
                    description,
                    expirationDate,
                    discount,
                    availableTime.start,
                    availableTime.end,
                )
        }
}
