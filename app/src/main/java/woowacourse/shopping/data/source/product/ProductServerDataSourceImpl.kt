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
        category: String?
    ): Pair<List<Product>, Boolean> {
        val response =
            retrofitProductService
                .requestProducts(page = startIndex, size = pageSize, category = category)

        check(response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            "products 요청 실패: ${response.code()},  Message: $errorBody"
        }
        val body =
            response.body()
                ?: error("empty body")
        return Pair(body.content.map { it.toDomain() }, body.last)
    }

    override suspend fun getProduct(id: Long): Product {
        val response =
            retrofitProductService
                .getProductDetail(id = id)

        check(response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            "products 요청 실패: ${response.code()},  Message: $errorBody"
        }
        val body =
            response.body()
                ?: error("empty body")
        return body.toDomain()
    }

}
