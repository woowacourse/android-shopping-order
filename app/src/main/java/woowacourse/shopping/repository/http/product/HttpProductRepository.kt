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
import woowacourse.shopping.model.Products
import woowacourse.shopping.repository.ProductRepository
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
    ): Products =
        withContext(Dispatchers.IO) {
            val safeFrom = fromIndex.coerceAtLeast(0)
            val safeLimit = limit.coerceAtLeast(0)
            val untilExclusive = safeFrom + safeLimit

            ensureProductsLoaded(untilExclusive)

            val safeTo = minOf(untilExclusive, cachedProducts.size)
            if (safeFrom >= safeTo) return@withContext Products(emptyList())

            Products(cachedProducts.subList(safeFrom, safeTo))
        }

    override suspend fun getProductsByCategory(
        category: String,
        limit: Int,
    ): Products =
        withContext(Dispatchers.IO) {
            val safeLimit = limit.coerceAtLeast(0)
            if (category.isBlank() || safeLimit == 0) return@withContext Products(emptyList())

            val responseBody =
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

            val fetchedProducts =
                runCatching {
                    responseBody
                        .content
                        .orEmpty()
                        .map { it.toProduct() }
                }.getOrElse { throwable ->
                    throw ProductParsingException("카테고리 상품 목록 응답을 파싱할 수 없습니다.", throwable)
                }

            cachedProducts = (cachedProducts + fetchedProducts).distinctBy { it.id }
            totalCount = maxOf(totalCount, cachedProducts.size.toLong())

            Products(fetchedProducts.take(safeLimit))
        }

    override suspend fun hasNext(current: Int): Boolean =
        withContext(Dispatchers.IO) {
            if (current < 0) return@withContext false

            ensureProductsLoaded(current + 2)
            current < size - 1
        }

    override suspend fun findAllByIds(ids: Set<Long>): Map<Long, Product> =
        withContext(Dispatchers.IO) {
            if (ids.isEmpty()) return@withContext emptyMap()

            val cachedProductsById = cachedProducts.associateBy { it.id }.toMutableMap()
            val missingIds = ids - cachedProductsById.keys

            missingIds.forEach { productId ->
                fetchProductById(productId)?.let { product ->
                    cachedProductsById[product.id] = product
                }
            }

            cachedProducts = cachedProductsById.values.toList()

            ids
                .mapNotNull { productId ->
                    cachedProductsById[productId]?.let { productId to it }
                }.toMap()
        }

    private suspend fun ensureProductsLoaded(untilExclusive: Int) {
        if (untilExclusive <= cachedProducts.size || lastPageLoaded) return

        while (cachedProducts.size < untilExclusive && !lastPageLoaded) {
            val responseBody =
                executeRequest(
                    errorMessage = "상품 목록 API 호출에 실패했습니다.",
                    request = { productApiService.getProducts(page = nextPage, size = NETWORK_PAGE_SIZE) },
                )

            val fetchedProducts =
                runCatching {
                    responseBody
                        .content
                        .orEmpty()
                        .map { it.toProduct() }
                }.getOrElse { throwable ->
                    throw ProductParsingException("상품 목록 응답을 파싱할 수 없습니다.", throwable)
                }

            cachedProducts = cachedProducts + fetchedProducts
            totalCount = responseBody.totalElements ?: cachedProducts.size.toLong()
            lastPageLoaded = responseBody.last ?: fetchedProducts.isEmpty()
            nextPage += 1
        }
    }

    private suspend fun fetchProductById(productId: Long): Product? {
        val responseBody =
            executeRequest(
                errorMessage = "상품 상세 API 호출에 실패했습니다.",
                request = { productApiService.getProduct(id = productId) },
            )

        val product =
            runCatching {
                responseBody.toProduct()
            }.getOrElse { throwable ->
                throw ProductParsingException("상품 상세 응답을 파싱할 수 없습니다.", throwable)
            }

        cachedProducts = (cachedProducts + product).distinctBy { it.id }
        totalCount = maxOf(totalCount, cachedProducts.size.toLong())
        return product
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
