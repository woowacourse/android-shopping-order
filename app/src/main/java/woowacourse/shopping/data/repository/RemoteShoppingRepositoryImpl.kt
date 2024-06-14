package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.client.ProductClient
import woowacourse.shopping.data.mapper.extractPageInfo
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.remote.model.dto.ProductDto
import woowacourse.shopping.domain.model.PageInfo
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductListInfo
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.data.remote.service.RetrofitService

class RemoteShoppingRepositoryImpl(private val service: RetrofitService = ProductClient.service) :
    ShoppingItemsRepository {
    override suspend fun fetchProductsWithPage(
        page: Int,
        size: Int,
    ): Result<ProductListInfo> {
        val response = service.requestProducts(page, size)
        return if (response.isSuccessful && response.body() != null) {
            val productResponseDto = response.body()
            val products = productResponseDto?.content?.map { it.toDomainModel() }.orEmpty()
            val pageInfo =
                productResponseDto?.extractPageInfo()
                    ?: PageInfo(false, 0, 0)
            val productListInfo = ProductListInfo(products, pageInfo)
            Result.success(productListInfo)
        } else {
            Result.failure(RuntimeException("network connection error. try again. code: ${response.code()}"))
        }
    }

    override suspend fun findProductItem(id: Long): Result<Product> {
        val response = service.requestProduct(id)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            val product = body.toDomainModel()
            Result.success(product)
        } else {
            Result.failure(RuntimeException("No such product. Check product id. code: ${response.code()}"))
        }
    }

    override suspend fun recommendProducts(
        category: String,
        count: Int,
        cartItemIds: List<Long>,
    ): List<Product> {
        val categoryProducts: MutableList<Product>
        var productDtoList: List<ProductDto>? = null
        val response =
            service.requestProductWithCategory(
                category = category,
                size = count + cartItemIds.size,
            )
        if (response.isSuccessful && response.body() != null) {
            productDtoList = response.body()?.content
        }
        categoryProducts = productDtoList?.map { it.toDomainModel() }.orEmpty().toMutableList()
        removeDuplicateItemsFromCart(categoryProducts, cartItemIds)
        return categoryProducts.take(count)
    }

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
