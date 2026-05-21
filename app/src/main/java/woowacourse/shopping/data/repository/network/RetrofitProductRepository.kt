package woowacourse.shopping.data.repository.network

import retrofit2.HttpException
import woowacourse.shopping.data.model.Money
import woowacourse.shopping.data.model.PageResult
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.remote.dto.ProductsResponse
import woowacourse.shopping.data.remote.dto.toDomain
import woowacourse.shopping.data.remote.service.ProductService
import woowacourse.shopping.data.repository.ProductRepository

class RetrofitProductRepository(
    private val service: ProductService,
) : ProductRepository {
    override suspend fun getProductPage(
        page: Int,
        count: Int,
    ): PageResult<Product> {
        val response = service.getProducts(page = page - 1, size = count)
        return response.toPageResult()
    }

    override suspend fun findProduct(id: Long): Product? {
        try {
            return service.getProduct(id = id).toDomain()
        } catch (_: HttpException) {
            return null
        }
    }
}

private fun ProductsResponse.toPageResult(): PageResult<Product> =
    PageResult(
        items =
            content.map {
                Product(
                    id = it.id,
                    name = it.name,
                    price = Money(it.price),
                    imageUrl = it.imageUrl,
                    category = it.category,
                )
            },
        currentPage = number + 1,
        pageSize = size,
        totalCount = totalElements,
        totalPages = totalPages,
    )
