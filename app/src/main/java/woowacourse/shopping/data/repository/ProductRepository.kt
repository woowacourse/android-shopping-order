package woowacourse.shopping.data.repository

import woowacourse.shopping.data.api.ProductApi
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.CatalogProduct
import woowacourse.shopping.domain.model.CatalogProduct.Companion.EMPTY_CATALOG_PRODUCT
import woowacourse.shopping.domain.model.CatalogProducts
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.ProductDetail
import woowacourse.shopping.domain.model.ProductDetail.Companion.EMPTY_PRODUCT_DETAIL
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepository(
    private val api: ProductApi,
) : ProductRepository {
    override fun fetchCatalogProduct(productId: Int): CatalogProduct {
        val productDetail: ProductDetail =
            api
                .getProductDetail(productId)
                .execute()
                .body()
                ?.toDomain() ?: EMPTY_PRODUCT_DETAIL
        return CatalogProduct(productDetail, 0)
    }

    override fun fetchCatalogProducts(productIds: List<Int>): List<CatalogProduct> = listOf(EMPTY_CATALOG_PRODUCT)

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
            page = Page(page, products?.first ?: false, products?.last ?: false),
        )
    }
}
