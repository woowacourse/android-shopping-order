package woowacourse.shopping.repository.http.repository

import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.repository.CouponRepository
import woowacourse.shopping.repository.http.api.CouponApiService
import woowacourse.shopping.repository.http.dto.coupon.toCoupon

private val NETWORK_JSON =
    Json {
        ignoreUnknownKeys = true
    }

class HttpCouponRepository(
    private val couponApiService: CouponApiService,
) : CouponRepository {
    constructor(
        client: OkHttpClient,
        baseUrlProvider: () -> HttpUrl,
    ) : this(
        couponApiService = createCouponApiService(client, baseUrlProvider),
    )

    constructor(
        client: OkHttpClient,
        baseUrl: String,
    ) : this(
        client = client,
        baseUrlProvider = {
            requireNotNull(baseUrl.toHttpUrlOrNull()) { "유효한 coupon API baseUrl이 필요합니다." }
        },
    )

    override suspend fun getCoupons(): Result<List<Coupon>> =
        runCatching {
            val response = couponApiService.getCoupons()
            if (response.isSuccessful) {
                response.body()?.map { it.toCoupon() } ?: emptyList()
            } else {
                throw Exception("쿠폰을 불러오는 데 실패했습니다.")
            }
        }

    companion object {
        private fun createCouponApiService(
            client: OkHttpClient,
            baseUrlProvider: () -> HttpUrl,
        ): CouponApiService =
            Retrofit
                .Builder()
                .baseUrl(baseUrlProvider())
                .client(client)
                .addConverterFactory(NETWORK_JSON.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(CouponApiService::class.java)
    }
}
