package woowacourse.shopping.data.product

import woowacourse.shopping.data.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto
import woowacourse.shopping.data.service.ApiFactory
import woowacourse.shopping.model.Product
import kotlin.concurrent.thread

class ProductRepositoryImpl : ProductRepository {
    override fun getProducts(
        page: Int,
        size: Int,
    ): Result<List<Product>> =
        runCatching {
            var productsDto: ResponseProductsGetDto? = null
            thread {
                productsDto = ApiFactory.getProductsByOffset(page, size)
            }.join()
            val products = productsDto ?: error("상품 정보를 불러오지 못했습니다")
            products.content.map { product ->
                Product(
                    id = product.id,
                    imageUrl = product.imageUrl,
                    name = product.name,
                    price = product.price,
                    category = product.category,
                )
            }
        }

    override fun find(id: Long): Result<Product> =
        runCatching {
            var productDto: ResponseProductIdGetDto? = null
            thread {
                productDto = ApiFactory.getProductsById(id)
            }.join()
            val product = productDto ?: error("$id 에 해당하는 productId가 없습니다")
            Product(
                id = product.id,
                imageUrl = product.imageUrl,
                name = product.name,
                price = product.price,
                category = product.category,
            )
        }

    override fun productsByCategory(category: String): Result<List<Product>> =
        runCatching {
            var page = 0
            val products = mutableListOf<Product>()
            var loadedProducts: List<Product>
            while (true) {
                loadedProducts =
                    getProducts(page, 20).getOrThrow().filter { it.category == category }
                        .toMutableList()
                if (loadedProducts.isEmpty()) break
                products.addAll(loadedProducts)
                page++
            }
            products
        }
}
