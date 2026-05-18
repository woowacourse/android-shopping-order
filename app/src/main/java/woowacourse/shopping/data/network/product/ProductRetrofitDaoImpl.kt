package woowacourse.shopping.data.network.product

import woowacourse.shopping.domain.Product

class ProductRetrofitDaoImpl(
    val retrofitProductService: RetrofitProductService,
) : ProductDao {
    override suspend fun findAllProduct(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): Pair<List<Product>, Boolean> {
        val response = retrofitProductService
            .requestProducts(page = startIndex, size = pageSize, category = category)

        check(response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            "products 요청 실패: ${response.code()},  Message: $errorBody"
        }
        val body = response.body()
            ?: error("empty body")
        return Pair(body.content.map { it.toDomain() }, body.last)
    }

    override suspend fun findById(id: Long): Product {
        val response = retrofitProductService
            .getProductDetail(id = id)

        check(response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            "products 요청 실패: ${response.code()},  Message: $errorBody"
        }
        val body = response.body()
            ?: error("empty body")
        return body.toDomain()
    }
}
