package woowacourse.shopping.repository.http.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
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
import woowacourse.shopping.repository.http.exception.CouponNetworkException
import woowacourse.shopping.repository.http.exception.CouponParsingException
import woowacourse.shopping.repository.http.exception.CouponResponseException
import java.io.IOException

private val NETWORK_JSON =
    Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
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
        withContext(Dispatchers.IO) {
            try {
                val response = couponApiService.getCoupons()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null) {
                        Result.failure(
                            CouponParsingException(
                                message = "쿠폰 API 응답 본문이 비어 있습니다.",
                                cause = IllegalStateException("response body is null"),
                            ),
                        )
                    } else {
                        Result.success(body.map { it.toCoupon() })
                    }
                } else {
                    Result.failure(
                        CouponResponseException(
                            code = response.code(),
                            message = "쿠폰 목록 API 호출에 실패했습니다. code=${response.code()}",
                        ),
                    )
                }
            } catch (exception: IOException) {
                Log.e("NetworkError", "Reason: ${exception.message}", exception)
                Result.failure(
                    CouponNetworkException(
                        message = "쿠폰 목록 API 호출 중 네트워크 오류가 발생했습니다.",
                        cause = exception,
                    ),
                )
            } catch (exception: SerializationException) {
                Result.failure(
                    CouponParsingException(
                        message = "쿠폰 API 응답 데이터가 올바르지 않습니다.",
                        cause = exception,
                    ),
                )
            } catch (exception: Exception) {
                Result.failure(exception)
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
