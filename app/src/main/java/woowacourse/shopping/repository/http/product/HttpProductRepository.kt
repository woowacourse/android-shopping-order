package woowacourse.shopping.repository.http.product

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
import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.query.ProductPageResult
import java.io.IOException

private val NETWORK_JSON =
    Json {
        ignoreUnknownKeys = true
    }

class HttpProductRepository(
    private val productApiService: ProductApiService,
) : ProductRepository {
    constructor(
        client: OkHttpClient,
        baseUrlProvider: () -> HttpUrl,
    ) : this(
        productApiService = createProductApiService(client, baseUrlProvider),
    )

    constructor(
        client: OkHttpClient,
        baseUrl: String,
    ) : this(
        client = client,
        baseUrlProvider = {
            requireNotNull(baseUrl.toHttpUrlOrNull()) { "유효한 상품 API baseUrl이 필요합니다." }
        },
    )

    override suspend fun getProducts(
        page: Int,
        size: Int,
    ): ProductPageResult = getProductPage(page = page, size = size)

    override suspend fun getProductsByCategory(
        category: String,
        page: Int,
        size: Int,
    ): ProductPageResult =
        withContext(Dispatchers.IO) {
            val safePage = page.coerceAtLeast(0)
            val safeSize = size.coerceAtLeast(0)
            if (category.isBlank() || safeSize == 0) {
                return@withContext ProductPageResult(
                    items = emptyList(),
                    totalElements = 0,
                    page = safePage,
                    size = safeSize,
                    hasNext = false,
                )
            }

            fetchProductPage(
                page = safePage,
                size = safeSize,
                category = category,
                errorMessage = "카테고리 상품 목록 API 호출에 실패했습니다.",
                parsingErrorMessage = "카테고리 상품 목록 응답을 파싱할 수 없습니다.",
            )
        }

    override suspend fun findAllByIds(ids: Set<Long>): Map<Long, Product> =
        withContext(Dispatchers.IO) {
            if (ids.isEmpty()) return@withContext emptyMap()

            ids
                .mapNotNull { productId ->
                    fetchProductById(productId)?.let { productId to it }
                }.toMap()
        }

    private suspend fun getProductPage(
        page: Int,
        size: Int,
    ): ProductPageResult =
        withContext(Dispatchers.IO) {
            val safePage = page.coerceAtLeast(0)
            val safeSize = size.coerceAtLeast(0)
            if (safeSize == 0) {
                return@withContext ProductPageResult(
                    items = emptyList(),
                    totalElements = 0,
                    page = safePage,
                    size = safeSize,
                    hasNext = false,
                )
            }

            fetchProductPage(
                page = safePage,
                size = safeSize,
                errorMessage = "상품 목록 API 호출에 실패했습니다.",
                parsingErrorMessage = "상품 목록 응답을 파싱할 수 없습니다.",
            )
        }

    private suspend fun fetchProductPage(
        page: Int,
        size: Int,
        category: String? = null,
        errorMessage: String,
        parsingErrorMessage: String,
    ): ProductPageResult {
        val responseBody =
            executeRequest(
                errorMessage = errorMessage,
                request = { productApiService.getProducts(page = page, size = size, category = category) },
            )

        val fetchedProducts =
            runCatching {
                responseBody
                    .content
                    .orEmpty()
                    .map { it.toDomain() }
            }.getOrElse { throwable ->
                throw ProductParsingException(parsingErrorMessage, throwable)
            }

        return ProductPageResult(
            items = fetchedProducts,
            totalElements = responseBody.totalElements?.coerceAtMost(Int.MAX_VALUE.toLong())?.toInt() ?: fetchedProducts.size,
            page = page,
            size = size,
            hasNext = responseBody.last?.not() ?: (fetchedProducts.size >= size),
        )
    }

    private suspend fun fetchProductById(productId: Long): Product? {
        val responseBody =
            executeRequest(
                errorMessage = "상품 상세 API 호출에 실패했습니다.",
                request = { productApiService.getProduct(id = productId) },
            )

        return runCatching {
            responseBody.toDomain()
        }.getOrElse { throwable ->
            throw ProductParsingException("상품 상세 응답을 파싱할 수 없습니다.", throwable)
        }
    }

    private suspend fun <T> executeRequest(
        errorMessage: String,
        request: suspend () -> Response<T>,
    ): T =
        try {
            val response = request()

            if (!response.isSuccessful) {
                throw ProductResponseException(
                    code = response.code(),
                    message = "$errorMessage code=${response.code()}",
                )
            }

            response.body()
                ?: throw ProductParsingException(
                    "상품 API 응답 본문이 비어 있습니다.",
                    IllegalStateException("response body is null"),
                )
        } catch (exception: ProductRemoteException) {
            throw exception
        } catch (exception: IOException) {
            throw ProductNetworkException(errorMessage, exception)
        } catch (exception: SerializationException) {
            throw ProductParsingException("상품 API 응답이 올바르지 않습니다.", exception)
        }

    companion object {
        private fun createProductApiService(
            client: OkHttpClient,
            baseUrlProvider: () -> HttpUrl,
        ): ProductApiService =
            Retrofit
                .Builder()
                .baseUrl(baseUrlProvider())
                .client(client)
                .addConverterFactory(NETWORK_JSON.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(ProductApiService::class.java)
    }
}
