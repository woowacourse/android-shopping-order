package woowacourse.shopping.data.repository

import woowacourse.shopping.data.api.ProductApi
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.CatalogProduct
import woowacourse.shopping.domain.model.CatalogProducts
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepository(
    private val api: ProductApi,
) : ProductRepository {
    override fun fetchCatalogProduct(productId: Int): CatalogProduct? = dao.getProduct(productId)?.toDomain()

    override fun fetchCatalogProducts(productIds: List<Int>): List<CatalogProduct> = dao.getProducts(productIds).map { it.toDomain() }

    override fun fetchProducts(
        page: Int,
        size: Int,
    ): CatalogProducts {
        val products =
            api
                .getProducts(
                    page = page,
                    size = size,
                ).execute()
                .body()

        return CatalogProducts(
            products = products?.content?.map { it.toDomain() } ?: emptyList(),
            currentPage = products?.pageable?.pageNumber ?: 0,
            hasMore = products?.last?.not() ?: false,
        )
    }
}
