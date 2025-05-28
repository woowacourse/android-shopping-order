package woowacourse.shopping.data.repository

import woowacourse.shopping.data.api.ProductApi
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Product.Companion.EMPTY_PRODUCT
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.model.ProductDetail.Companion.EMPTY_PRODUCT_DETAIL
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepository(
    private val api: ProductApi,
) : ProductRepository {
    override fun fetchCatalogProduct(productId: Long): Product {
        val productDetail: ProductDetail =
            api
                .getProductDetail(productId)
                .execute()
                .body()
                ?.toDomain() ?: EMPTY_PRODUCT_DETAIL
        return Product(productDetail)
    }

    override fun fetchCatalogProducts(productIds: List<Int>): List<Product> = listOf(EMPTY_PRODUCT)

    override fun fetchProducts(
        page: Int,
        size: Int,
    ): Products {
        val products =
            api
                .getProducts(
                    page = page,
                    size = size,
                ).execute()
                .body()

        return Products(
            products = products?.content?.map { it.toDomain() } ?: emptyList(),
            page = Page(page, products?.first ?: false, products?.last ?: false),
        )
    }

    override fun fetchAllProducts(): List<Product> {
        val firstPage: Int = 0
        val maxSize: Int = Int.MAX_VALUE

        val products =
            api
                .getProducts(
                    page = firstPage,
                    size = maxSize,
                ).execute()
                .body()

        return products?.content?.map { it.toDomain() } ?: emptyList()
    }
}
