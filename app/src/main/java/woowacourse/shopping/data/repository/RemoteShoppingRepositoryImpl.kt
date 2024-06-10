package woowacourse.shopping.data.repository

import android.util.Log
import woowacourse.shopping.data.database.ProductClient
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.model.dto.ProductResponseDto
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ShoppingItemsRepository

class RemoteShoppingRepositoryImpl : ShoppingItemsRepository {
    private val service = ProductClient.service
    private var productData: ProductResponseDto? = null
    private var products: List<Product>? = null

    /*init {
        initializeProducts()

        productData = service.requestProducts().execute().body()
        products = productData?.content?.map { it.toDomainModel() }
    }*/

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
        /*runCatching {
            Log.d("crong", "fetchProduct")
            val productData = service.requestProducts()

            Log.d("crong", "$productData")
            productData?.totalElements ?: 0
        }*/
        runCatching {
            Log.d("crong", "fetchProduct")
            val response = service.requestProducts()
            if (response.isSuccessful) {
                val productData = response.body()
                Log.d("crong", "$productData")
                productData?.totalElements ?: 0
            } else {
                throw Exception("Failed to fetch products: ${response.errorBody()?.string()}")
            }
        }

    /*{
        return productData?.totalElements ?: 0
    }*/

    override suspend fun fetchProductsWithIndex(
        start: Int,
        end: Int,
    ): Result<List<Product>> =
        runCatching {
            val response = service.requestProducts()
            var products: List<Product> = emptyList<Product>()
            Log.d("crong", "response: $response")
            if (response.isSuccessful) {
                val productData = response.body()
                Log.d("crong", "$productData")
                products = productData?.content?.map { it.toDomainModel() } ?: emptyList()
                Log.d("crong", "products: $products")
                Log.d("crong", "start : $start, end : $end")
                Log.d("crong", "products.size : ${products.subList(start, end).size} ")
                products.subList(start, end)
            } else {
                throw Exception("Failed to fetch products: ${response.errorBody()?.string()}")
            }

            // products?.subList(start, end) ?: emptyList()
        }

    /*{
        return products?.subList(start, end) ?: emptyList()
    }*/

    override suspend fun findProductItem(id: Long): Result<Product?> =
        runCatching {
            /*Log.d("crong", "findProductItem: $id")
            Log.d("crong", "service: $service")
            Log.d("crong", "service.requestProduct(id): ${service.requestProduct(id).execute().body()}")
            service.requestProduct(id).execute().body()?.toDomainModel()*/

            val response = service.requestProduct(id)
            if (response.isSuccessful) {
                response.body()?.toDomainModel()
            } else {
                throw Exception("Failed to fetch product: ${response.errorBody()?.string()}")
            }
        }

    /*{
        var product: Product? = null
        threadAction {
            product = service.requestProduct(id).execute().body()?.toDomainModel()
        }

        return product
    }*/

    override suspend fun recommendProducts(
        category: String,
        count: Int,
        cartItemIds: List<Long>,
    ): Result<List<Product>> =
        runCatching {
            var categoryProducts: MutableList<Product> = mutableListOf()

            val response =
                service.requestProductWithCategory(
                    category = category,
                    size = count + cartItemIds.size,
                )

            if (response.isSuccessful) {
                productData = response.body()
            } else {
                throw Exception("Failed to fetch products: ${response.errorBody()?.string()}")
            }

            categoryProducts =
                productData?.content?.map { it.toDomainModel() }.orEmpty().toMutableList()
            removeDuplicateItemsFromCart(categoryProducts, cartItemIds)
            categoryProducts.take(count)
        }

    /*{
        var categoryProducts: MutableList<Product> = mutableListOf()
        threadAction {
            productData =
                service.requestProductWithCategory(
                    category = category,
                    size = count + cartItemIds.size,
                ).execute().body()
        }
        categoryProducts =
            productData?.content?.map { it.toDomainModel() }.orEmpty().toMutableList()
        removeDuplicateItemsFromCart(categoryProducts, cartItemIds)
        return categoryProducts.take(count)
    }*/

    private fun removeDuplicateItemsFromCart(
        categoryProducts: MutableList<Product>,
        cartItemIds: List<Long>,
    ) {
        if (categoryProducts.isNotEmpty()) {
            cartItemIds.forEach { cartItemId ->
                categoryProducts.removeIf { it.id == cartItemId }
            }
        }
    }
}
