package woowacourse.shopping.repository.http.repository

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
import woowacourse.shopping.model.Products
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.http.api.ProductApiService
import woowacourse.shopping.repository.http.exception.ProductNetworkException
import woowacourse.shopping.repository.http.exception.ProductParsingException
import woowacourse.shopping.repository.http.exception.ProductRemoteException
import woowacourse.shopping.repository.http.exception.ProductResponseException
import java.io.IOException

private const val NETWORK_PAGE_SIZE = 20
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

    @Volatile
    private var cachedProducts: List<Product> = emptyList()

    @Volatile
    private var totalCount: Long = 0L

    @Volatile
    private var nextPage: Int = 0

    @Volatile
    private var lastPageLoaded: Boolean = false

    override val size: Int
        get() = maxOf(totalCount.coerceAtMost(Int.MAX_VALUE.toLong()), cachedProducts.size.toLong()).toInt()

    override suspend fun getProducts(
        fromIndex: Int,
        limit: Int,
    ): Result<Products> =
        withContext(Dispatchers.IO) {
            val safeFrom = fromIndex.coerceAtLeast(0)
            val safeLimit = limit.coerceAtLeast(0)
            val untilExclusive = safeFrom + safeLimit

            val loadResult = ensureProductsLoaded(untilExclusive)
            if (loadResult.isFailure) {
                return@withContext Result.failure(loadResult.exceptionOrNull()!!)
            }

            val safeTo = minOf(untilExclusive, cachedProducts.size)

            if (safeFrom >= safeTo) {
                Result.success(Products(emptyList()))
            } else {
                Result.success(Products(cachedProducts.subList(safeFrom, safeTo)))
            }
        }

    override suspend fun getProductsByCategory(
        category: String,
        limit: Int,
    ): Result<Products> =
        withContext(Dispatchers.IO) {
            val safeLimit = limit.coerceAtLeast(0)
            if (category.isBlank() || safeLimit == 0) {
                return@withContext Result.success(Products(emptyList()))
            }

            val responseResult =
                executeRequest(
                    errorMessage = "카테고리 상품 목록 API 호출에 실패했습니다.",
                    request = {
                        productApiService.getProducts(
                            page = 0,
                            size = safeLimit,
                            category = category,
                        )
                    },
                )

            if (responseResult.isFailure) {
                return@withContext Result.failure(responseResult.exceptionOrNull()!!)
            }

            val responseBody = responseResult.getOrNull()!!

            val fetchedProducts =
                try {
                    responseBody
                        .content
                        .orEmpty()
                        .map { it.toProduct() }
                } catch (throwable: Throwable) {
                    return@withContext Result.failure(
                        ProductParsingException(
                            message = "카테고리 상품 목록 응답을 파싱할 수 없습니다.",
                            cause = throwable,
                        ),
                    )
                }

            cachedProducts = (cachedProducts + fetchedProducts).distinctBy { it.id }
            totalCount = maxOf(totalCount, cachedProducts.size.toLong())

            Result.success(Products(fetchedProducts.take(safeLimit)))
        }

    override suspend fun hasNext(current: Int): Result<Boolean> =
        withContext(Dispatchers.IO) {
            if (current < 0) return@withContext Result.success(false)

            val loadResult = ensureProductsLoaded(current + 2)
            if (loadResult.isFailure) {
                return@withContext Result.failure(loadResult.exceptionOrNull()!!)
            }

            Result.success(current < size - 1)
        }

    override suspend fun findAllByIds(ids: Set<Long>): Result<Map<Long, Product>> =
        withContext(Dispatchers.IO) {
            if (ids.isEmpty()) return@withContext Result.success(emptyMap())

            val cachedProductsById = cachedProducts.associateBy { it.id }.toMutableMap()
            val missingIds = ids - cachedProductsById.keys

            for (productId in missingIds) {
                val productResult = fetchProductById(productId)

                if (productResult.isFailure) {
                    return@withContext Result.failure(productResult.exceptionOrNull()!!)
                }

                val product = productResult.getOrNull()
                if (product != null) {
                    cachedProductsById[product.id] = product
                }
            }

            cachedProducts = cachedProductsById.values.toList()

            val result =
                ids
                    .mapNotNull { productId ->
                        cachedProductsById[productId]?.let { productId to it }
                    }.toMap()

            Result.success(result)
        }

    private suspend fun ensureProductsLoaded(untilExclusive: Int): Result<Unit> {
        if (untilExclusive <= cachedProducts.size || lastPageLoaded) {
            return Result.success(Unit)
        }

        while (cachedProducts.size < untilExclusive && !lastPageLoaded) {
            val responseResult =
                executeRequest(
                    errorMessage = "상품 목록 API 호출에 실패했습니다.",
                    request = { productApiService.getProducts(page = nextPage, size = NETWORK_PAGE_SIZE) },
                )

            if (responseResult.isFailure) {
                return Result.failure(responseResult.exceptionOrNull()!!)
            }

            val responseBody = responseResult.getOrNull()!!

            val fetchedProducts =
                try {
                    responseBody
                        .content
                        .orEmpty()
                        .map { it.toProduct() }
                } catch (throwable: Throwable) {
                    return Result.failure(
                        ProductParsingException(
                            message = "상품 목록 응답을 파싱할 수 없습니다.",
                            cause = throwable,
                        ),
                    )
                }

            cachedProducts = cachedProducts + fetchedProducts
            totalCount = responseBody.totalElements ?: cachedProducts.size.toLong()
            lastPageLoaded = responseBody.last ?: fetchedProducts.isEmpty()
            nextPage += 1
        }

        return Result.success(Unit)
    }

    private suspend fun fetchProductById(productId: Long): Result<Product> {
        val responseResult =
            executeRequest(
                errorMessage = "상품 상세 API 호출에 실패했습니다.",
                request = { productApiService.getProduct(id = productId) },
            )

        if (responseResult.isFailure) {
            return Result.failure(responseResult.exceptionOrNull()!!)
        }

        val responseBody = responseResult.getOrNull()!!

        val product =
            try {
                responseBody.toProduct()
            } catch (throwable: Throwable) {
                return Result.failure(
                    ProductParsingException(
                        message = "상품 상세 응답을 파싱할 수 없습니다.",
                        cause = throwable,
                    ),
                )
            }

        cachedProducts = (cachedProducts + product).distinctBy { it.id }
        totalCount = maxOf(totalCount, cachedProducts.size.toLong())

        return Result.success(product)
    }

    private suspend fun <T> executeRequest(
        errorMessage: String,
        request: suspend () -> Response<T>,
    ): Result<T> =
        try {
            val response = request()

            if (!response.isSuccessful) {
                Result.failure(
                    ProductResponseException(
                        code = response.code(),
                        message = "$errorMessage code=${response.code()}",
                    ),
                )
            } else {
                val body = response.body()

                if (body == null) {
                    Result.failure(
                        ProductParsingException(
                            message = "상품 API 응답 본문이 비어 있습니다.",
                            cause = IllegalStateException("response body is null"),
                        ),
                    )
                } else {
                    Result.success(body)
                }
            }
        } catch (exception: ProductRemoteException) {
            Result.failure(exception)
        } catch (exception: IOException) {
            Result.failure(
                ProductNetworkException(
                    message = errorMessage,
                    cause = exception,
                ),
            )
        } catch (exception: SerializationException) {
            Result.failure(
                ProductParsingException(
                    message = "상품 API 응답이 올바르지 않습니다.",
                    cause = exception,
                ),
            )
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
