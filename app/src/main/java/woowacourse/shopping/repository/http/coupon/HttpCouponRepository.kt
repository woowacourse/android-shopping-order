package woowacourse.shopping.repository.http.coupon

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import woowacourse.shopping.model.Coupon
import woowacourse.shopping.repository.CouponRepository
import java.io.IOException

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
            requireNotNull(baseUrl.toHttpUrlOrNull()) { "유효한 쿠폰 API baseUrl이 필요합니다." }
        },
    )

    override suspend fun getCoupons(): List<Coupon> =
        withContext(Dispatchers.IO) {
            val responseBody =
                executeRequest(
                    errorMessage = "쿠폰 목록 API 호출에 실패했습니다.",
                    request = couponApiService::getCoupons,
                )

            runCatching {
                responseBody.coupons.map(CouponResponseDto::toDomain)
            }.getOrElse { throwable ->
                throw CouponParsingException("쿠폰 목록 응답을 파싱할 수 없습니다.", throwable)
            }
        }

    private suspend fun <T> executeRequest(
        errorMessage: String,
        request: suspend () -> Response<T>,
    ): T =
        try {
            val response = request()

            if (!response.isSuccessful) {
                throw CouponResponseException(
                    code = response.code(),
                    message = "$errorMessage code=${response.code()}",
                )
            }

            response.body()
                ?: throw CouponParsingException(
                    "쿠폰 API 응답 본문이 비어 있습니다.",
                    IllegalStateException("response body is null"),
                )
        } catch (exception: CouponRemoteException) {
            throw exception
        } catch (exception: IOException) {
            throw CouponNetworkException(errorMessage, exception)
        } catch (exception: SerializationException) {
            throw CouponParsingException("쿠폰 API 응답이 올바르지 않습니다.", exception)
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
