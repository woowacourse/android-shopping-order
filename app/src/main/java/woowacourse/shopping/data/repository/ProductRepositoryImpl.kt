package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ProductDataSource
import woowacourse.shopping.data.datasource.impl.ProductDataSourceImpl
import woowacourse.shopping.data.remote.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseProductsGetDto
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import kotlin.concurrent.thread

class ProductRepositoryImpl(private val dataSource: ProductDataSource = ProductDataSourceImpl()) :
    ProductRepository {
    override fun getProducts(
        page: Int,
        size: Int,
    ): List<Product> {
        var productsDto: ResponseProductsGetDto? = null
        thread {
            productsDto = dataSource.getProductsByOffset(page, size)
        }.join()
        val products = productsDto ?: error("상품 정보를 불러오지 못했습니다")
        return products.content.map { product ->
            Product(
                id = product.id,
                imageUrl = product.imageUrl,
                name = product.name,
                price = product.price,
                category = product.category,
            )
        }
    }

    override fun find(id: Long): Product {
        var productDto: ResponseProductIdGetDto? = null
        thread {
            productDto = dataSource.getProductsById(id)
        }.join()
        val product = productDto ?: error("$id 에 해당하는 productId가 없습니다")
        return Product(
            id = product.id,
            imageUrl = product.imageUrl,
            name = product.name,
            price = product.price,
            category = product.category,
        )
    }

    override fun productsByCategory(category: String): List<Product> {
        var page = 0
        var products = mutableListOf<Product>()
        var loadedProducts = listOf<Product>()
        while (true) {
            loadedProducts =
                getProducts(page, 20).filter { it.category == category }.toMutableList()
            if (loadedProducts.isEmpty()) break
            products.addAll(loadedProducts)
            page++
        }
        return products
    }
}
