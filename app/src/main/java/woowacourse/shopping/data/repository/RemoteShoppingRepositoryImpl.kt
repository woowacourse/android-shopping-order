package woowacourse.shopping.data.repository

import woowacourse.shopping.data.database.ProductClient
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.model.dto.ProductResponseDto
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ShoppingItemsRepository

class RemoteShoppingRepositoryImpl : ShoppingItemsRepository {
    private val service = ProductClient.service
    private var productData: ProductResponseDto? = null
    private var products: List<Product>? = null

    override suspend fun initializeProducts(): Result<Unit> =
        runCatching {
            val data = service.requestProducts()
            if (data.isSuccessful) {
                productData = data.body()
                products = productData?.content?.map { it.toDomainModel() }
            } else {
                throw Exception("Failed to fetch products: ${data.errorBody()?.string()}")
            }
            products = productData?.content?.map { it.toDomainModel() }
        }

    override suspend fun fetchProductsSize(): Result<Int> =
        runCatching {
            val response = service.requestProducts()
            if (response.isSuccessful) {
                val productData = response.body()
                productData?.totalElements ?: 0
            } else {
                throw Exception("Failed to fetch products: ${response.errorBody()?.string()}")
            }
        }

    override suspend fun fetchProductsWithIndex(
        start: Int,
        end: Int,
    ): Result<List<Product>> =
        runCatching {
            val response = service.requestProducts()
            var products: List<Product> = emptyList<Product>()
            if (response.isSuccessful) {
                val productData = response.body()
                products = productData?.content?.map { it.toDomainModel() } ?: emptyList()
                products.subList(start, end)
            } else {
                throw Exception("Failed to fetch products: ${response.errorBody()?.string()}")
            }
        }

    override suspend fun findProductItem(id: Long): Result<Product?> =
        runCatching {
            val response = service.requestProduct(id)
            if (response.isSuccessful) {
                response.body()?.toDomainModel()
            } else {
                throw Exception("Failed to fetch product: ${response.errorBody()?.string()}")
            }
        }

    override suspend fun recommendProducts(
        category: String,
        count: Int,
        productIds: List<Long>,
    ): Result<List<Product>> =
        runCatching {
            var categoryProducts: MutableList<Product> = mutableListOf()

            val response =
                service.requestProductWithCategory(
                    category = category,
                    size = count + productIds.size,
                )

            if (response.isSuccessful) {
                productData = response.body()
            } else {
                throw Exception("Failed to fetch products: ${response.errorBody()?.string()}")
            }

            categoryProducts =
                productData?.content?.map { it.toDomainModel() }.orEmpty().toMutableList()
            removeDuplicateItemsFromCart(categoryProducts, productIds)
            categoryProducts.take(count)
        }

    private fun removeDuplicateItemsFromCart(
        categoryProducts: MutableList<Product>,
        productIds: List<Long>,
    ) {
        if (categoryProducts.isNotEmpty()) {
            productIds.forEach { productId ->
                categoryProducts.removeIf { it.id == productId }
            }
        }
    }
}
