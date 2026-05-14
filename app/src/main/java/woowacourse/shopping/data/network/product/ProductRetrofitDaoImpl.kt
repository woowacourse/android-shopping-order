package woowacourse.shopping.data.network.product

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.domain.Product

class ProductRetrofitDaoImpl(
    val retrofitProductService: RetrofitProductService,
) : ProductDao {
    override suspend fun findAllProduct(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
        category: String?,
    ): List<Product> = withContext(Dispatchers.IO) {
        val response = retrofitProductService
            .requestProducts(page = startIndex, size = pageSize, category = category)
            .execute()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }
        val body = response.body()
            ?: error("empty body")
        body.content.map { it.toDomain() }
    }

    override suspend fun findById(id: String): Product = withContext(Dispatchers.IO) {
        val response = retrofitProductService
            .getProductDetail(id = id)
            .execute()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }
        val body = response.body()
            ?: error("empty body")
        body.toDomain()
    }
}
