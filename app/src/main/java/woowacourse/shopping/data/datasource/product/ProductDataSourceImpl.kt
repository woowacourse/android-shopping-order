package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.data.mapper.toProduct
import woowacourse.shopping.data.model.response.product.ProductContent
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.domain.Page
import woowacourse.shopping.domain.Product

class ProductDataSourceImpl(
    private val productService: ProductService,
) : ProductDataSource {
    override suspend fun fetchProduct(id: Long): Product {
        return productService.getProduct(id).toProduct()
    }

    override suspend fun fetchPageOfProducts(
        pageIndex: Int,
        pageSize: Int,
    ): Page<Product> {
        val response = productService.getProducts(page = pageIndex, size = pageSize)
        val products = response.productContent.map(ProductContent::toProduct)
        return Page(products, response.first, response.last)
    }
}
