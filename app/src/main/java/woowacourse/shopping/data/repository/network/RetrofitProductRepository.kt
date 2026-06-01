package woowacourse.shopping.data.repository.network

import woowacourse.shopping.data.remote.dto.toDomain
import woowacourse.shopping.data.remote.service.ProductService
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Page
import woowacourse.shopping.model.product.Product

class RetrofitProductRepository(
    private val service: ProductService,
) : ProductRepository {
    override suspend fun getProducts(
        page: Int,
        size: Int,
    ): Page<Product> {
        val response = service.getProducts(page = page, size = size)
        return Page(
            items =
                response.content.map {
                    Product(
                        id = it.id,
                        name = it.name,
                        price = Money(it.price),
                        imageUrl = it.imageUrl,
                        category = it.category,
                    )
                },
            isLast = response.last,
            totalPages = response.totalPages,
            currentPage = response.number,
            totalElements = response.totalElements,
        )
    }

    override suspend fun findProduct(id: Long): Product = service.getProduct(id = id).toDomain()
}
