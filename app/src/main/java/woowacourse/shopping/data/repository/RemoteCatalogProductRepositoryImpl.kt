package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CatalogRemoteDataSource
import woowacourse.shopping.data.dto.product.toUiModel
import woowacourse.shopping.product.catalog.ProductUiModel

class RemoteCatalogProductRepositoryImpl(
    private val remoteDataSource: CatalogRemoteDataSource,
) : CatalogProductRepository {
    override fun getRecommendedProducts(
        category: String,
        page: Int,
        size: Int,
        callback: (List<ProductUiModel>) -> Unit,
    ) {
        remoteDataSource.fetchProducts(
            category = category,
            page = page,
            size = size,
            onSuccess = { response ->
                val products = response.productContent.map { it.toUiModel() }
                callback(products)
            },
            onFailure = {
                callback(emptyList())
            },
        )
    }

    override fun getAllProductsSize(callback: (Long) -> Unit) {
        remoteDataSource.fetchAllProducts(
            onSuccess = { response -> callback(response.totalElements) },
            onFailure = { callback(0) },
        )
    }

    override fun getCartProductsByIds(
        productIds: List<Long>,
        callback: (List<ProductUiModel>) -> Unit,
    ) {
        val resultsMap = mutableMapOf<Long, ProductUiModel>()
        var completedCount = 0

        if (productIds.isEmpty()) {
            callback(emptyList())
            return
        }

        productIds.forEach { uid ->
            getProduct(
                productId = uid,
                onSuccess = { product ->
                    resultsMap[uid] = product
                    completedCount++
                    if (completedCount == productIds.size) {
                        callback(productIds.mapNotNull { resultsMap[it] })
                    }
                },
                onFailure = {
                    completedCount++
                    if (completedCount == productIds.size) {
                        callback(productIds.mapNotNull { resultsMap[it] })
                    }
                },
            )
        }
    }

    override fun getProductsByPage(
        page: Int,
        size: Int,
        callback: (List<ProductUiModel>) -> Unit,
    ) {
        remoteDataSource.fetchProducts(
            category = null,
            page = page,
            size = size,
            onSuccess = { response ->
                val products = response.productContent.map { it.toUiModel() }
                callback(products)
            },
            onFailure = { callback(emptyList()) },
        )
    }

    override fun getProduct(
        productId: Long,
        onSuccess: (ProductUiModel) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteDataSource.fetchProductDetail(
            id = productId,
            onSuccess = { content -> onSuccess(content.toUiModel()) },
            onFailure = { onFailure() },
        )
    }
}
