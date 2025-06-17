package woowacourse.shopping.data.repository

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.product.Content
import woowacourse.shopping.data.dto.product.ProductResponse
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.data.service.RetrofitProductService
import woowacourse.shopping.product.catalog.ProductUiModel
import woowacourse.shopping.util.toResult

class RemoteCatalogProductRepositoryImpl : CatalogProductRepository {
    val retrofitService = RetrofitProductService.INSTANCE.create(ProductService::class.java)

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
        return uids.mapNotNull { uid ->
            runCatching {
                getProduct(uid) as ProductUiModel
            }.getOrNull()
        }
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
    ): ProductUiModel? {
        val response = retrofitService
            .requestDetailProduct(
                id = id,
            ).toResult().getOrThrow()

        val body: Content = response.body() ?: return null
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
