package woowacourse.shopping.data.repository

import woowacourse.shopping.data.database.ProductClient
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.repository.CouponRepository

class RemoteCouponRepositoryImpl : CouponRepository {
    private val service = ProductClient.service

    override suspend fun getCoupons(): Result<List<CouponState>> =
        runCatching {
            val response = service.getCoupons()
            if (response.isSuccessful) {
                response.body()?.map { it.toDomainModel() } ?: emptyList()
            } else {
                emptyList()
            }
        }
}
