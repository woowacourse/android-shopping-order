package woowacourse.shopping.data.remote.retrofit.repository

import android.os.SystemClock
import woowacourse.shopping.data.mapper.toDomainProduct
import woowacourse.shopping.data.mapper.toDomainProducts
import woowacourse.shopping.data.remote.retrofit.api.ProductRetrofitInterface
import woowacourse.shopping.data.remote.retrofit.dto.ProductResponse
import woowacourse.shopping.domain.model.Product

class ProductRetrofitRepository(
    private val apiService: ProductRetrofitInterface,
) {
    private var hasLoadedProductsOnce: Boolean = false
    private var lastProductsLoadedElapsedMs: Long = 0L

    suspend fun requestProductPage(
        page: Int,
        size: Int,
        sort: List<String>? = null,
        category: String?,
    ): ProductPageResult {
        val response: ProductResponse =
            apiService.requestProducts(
                page = page,
                size = size,
                sort = sort,
                category = category,
            )
        return ProductPageResult(
            products = response.toDomainProducts(),
            hasNextPage = !response.last,
        )
    }

    suspend fun requestAllProducts(
        startPage: Int = DEFAULT_PAGE,
        size: Int = DEFAULT_SIZE,
        category: String?,
        force: Boolean = false,
    ): List<Product>? {
        if (shouldSkipProductRequest(force = force)) return null

        val loadedProducts = mutableListOf<Product>()
        var page = startPage
        var hasNextPage: Boolean
        do {
            val pageResult =
                requestProductPage(
                    page = page,
                    size = size,
                    category = category,
                )
            loadedProducts += pageResult.products
            hasNextPage = pageResult.hasNextPage
            page += 1
        } while (hasNextPage)
        markProductsLoaded()
        return loadedProducts
    }

    suspend fun requestProductDetail(id: Long): Product =
        apiService.requestProductDetail(
            id = id,
        ).toDomainProduct()

    private fun shouldSkipProductRequest(force: Boolean): Boolean {
        if (force) return false
        if (!hasLoadedProductsOnce) return false
        return isProductsCacheFresh()
    }

    private fun isProductsCacheFresh(): Boolean =
        SystemClock.elapsedRealtime() - lastProductsLoadedElapsedMs < PRODUCTS_CACHE_DURATION_MS

    private fun markProductsLoaded() {
        hasLoadedProductsOnce = true
        lastProductsLoadedElapsedMs = SystemClock.elapsedRealtime()
    }

    data class ProductPageResult(
        val products: List<Product>,
        val hasNextPage: Boolean,
    )

    private companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 20
        private const val PRODUCTS_CACHE_DURATION_MS = 30_000L
    }
}
