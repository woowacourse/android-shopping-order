package woowacourse.shopping.data.source

import android.util.Log
import woowacourse.shopping.data.dto.product.Content
import woowacourse.shopping.data.dto.product.ProductResponse
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.data.service.RetrofitProductService
import woowacourse.shopping.product.catalog.ProductUiModel

class CatalogProductDataSourceRemoteDataSource(
    private val retrofitService: ProductService =
        RetrofitProductService.INSTANCE.create(
            ProductService::class.java,
        ),
) : CatalogProductDataSource {
    override suspend fun getRecommendedProducts(
        category: String,
        page: Int,
        size: Int,
    ): List<ProductUiModel> {
        val response = retrofitService
            .requestProducts(
                category = category,
                page = page,
                size = size,
            )

        if (!response.isSuccessful) {
            Log.d("error", "error : $response")
        }

        val body: ProductResponse? = response.body()
        val content: List<Content>? = body?.content
        val products: List<ProductUiModel> =
            content?.mapNotNull {
                ProductUiModel(
                    id = it.id.toInt(),
                    imageUrl = it.imageUrl,
                    name = it.name,
                    price = it.price,
                )
            } ?: return emptyList()
        return products
    }

    override suspend fun getAllProductsSize(): Int {
        val response = retrofitService.requestProducts()


        val body: ProductResponse? = response.body()
        return body?.totalElements?.toInt() ?: 0
    }

    override suspend fun getCartProductsByUids(
        uids: List<Int>,
    ): List<ProductUiModel> {
        val resultsMap = mutableMapOf<Int, ProductUiModel>()
        var completedCount = 0

        if (uids.isEmpty()) {
            return emptyList()
        }

        uids.forEach { uid ->
            val product = getProduct(uid)
            resultsMap[uid] = product as ProductUiModel
            completedCount++
            if (completedCount == uids.size) {
                val orderedResults = uids.mapNotNull { resultsMap[it] }
                return orderedResults
            }
        }
        return emptyList()
    }

    override suspend fun getProductsByPage(
        page: Int,
        size: Int,
    ): List<ProductUiModel> {
        val response = retrofitService
            .requestProducts(
                page = page,
                size = size,
            )

        if (!response.isSuccessful) {
            Log.d("error", "error : $response")
        }

        val body: ProductResponse? = response.body()
        val content: List<Content>? = body?.content
        val products: List<ProductUiModel>? =
            content?.mapNotNull {
                ProductUiModel(
                    id = it.id.toInt(),
                    imageUrl = it.imageUrl,
                    name = it.name,
                    price = it.price,
                )
            }
        return products ?: emptyList()
    }

    override suspend fun getProduct(
        id: Int,
    ): ProductUiModel {
        val response = retrofitService
            .requestDetailProduct(
                id = id,
            )

        if (!response.isSuccessful) {
            Log.d("error", "error : $response")
        }

        val body: Content = response.body() ?: return ProductUiModel(
            id = 0,
            name = "[병천아우내] 모듬순대",
            price = 11900,
            category = "음식",
            imageUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/00fb05f8-cb19-4d21-84b1-5cf6b9988749.jpg",
        )
        val product =
            ProductUiModel(
                id = body.id.toInt(),
                imageUrl = body.imageUrl,
                name = body.name,
                price = body.price,
                category = body.category,
            )
        return product
    }
}
