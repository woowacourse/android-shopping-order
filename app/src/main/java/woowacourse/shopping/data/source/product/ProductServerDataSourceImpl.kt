package woowacourse.shopping.data.source.product

import woowacourse.shopping.data.network.product.RetrofitProductService
import woowacourse.shopping.domain.Product

class ProductServerDataSourceImpl(
    val retrofitProductService: RetrofitProductService,
) : ProductDataSource {
    override suspend fun loadProducts(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): Pair<List<Product>, Boolean> {
        val body =
            retrofitProductService
                .requestProducts(page = startIndex, size = pageSize, category = category)

        return Pair(body.content.map { it.toDomain() }, body.last)
    }

    override suspend fun getProduct(id: Long): Product {
        val body =
            retrofitProductService
                .getProductDetail(id = id)

        return body.toDomain()
    }
}
