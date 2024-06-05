package woowacourse.shopping.data.repository

import woowacourse.shopping.data.database.ProductClient
import woowacourse.shopping.data.mapper.extractPageInfo
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.model.dto.ProductDto
import woowacourse.shopping.domain.model.PageInfo
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductListInfo
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.domain.service.RetrofitService
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class RemoteShoppingRepositoryImpl(private val service: RetrofitService = ProductClient.service) :
    ShoppingItemsRepository {
    fun fetchProductsWithPage(
        page: Int,
        size: Int,
        resultCallBack: (ProductListInfo) -> Unit,
    ) {
        var productListInfo: ProductListInfo? = null
        threadAction {
            val response = service.requestProducts(page, size).execute()
            if (response.isSuccessful && response.body() != null) {
                val productResponseDto = response.body()
                val products = productResponseDto?.content?.map { it.toDomainModel() }.orEmpty()
                val pageInfo =
                    productResponseDto?.extractPageInfo()
                        ?: PageInfo(false, 0, 0)
                productListInfo = ProductListInfo(products, pageInfo)
            }
        }
        productListInfo?.let { resultCallBack(it) }
    }

    override fun fetchProductsWithPage(
        page: Int,
        size: Int,
    ): Result<ProductListInfo> {
        var result: Result<ProductListInfo>? = null
        var productListInfo: ProductListInfo
        threadAction {
            val response = service.requestProducts(page, size).execute()
            if (response.isSuccessful && response.body() != null) {
                val productResponseDto = response.body()
                val products = productResponseDto?.content?.map { it.toDomainModel() }.orEmpty()
                val pageInfo =
                    productResponseDto?.extractPageInfo()
                        ?: PageInfo(false, 0, 0)
                productListInfo = ProductListInfo(products, pageInfo)
                result = Result.success(productListInfo)
            }
        }
        return result ?: Result.failure(RuntimeException("network connection error. try again."))
    }

    override fun findProductItem(id: Long): Product? {
        var product: Product? = null
        threadAction {
            val response = service.requestProduct(id).execute()
            if (response.isSuccessful && response.body() != null) {
                product = response.body()?.toDomainModel()
            }
        }
        return product
    }

    override fun recommendProducts(
        category: String,
        count: Int,
        cartItemIds: List<Long>,
    ): List<Product> {
        val categoryProducts: MutableList<Product>
        var productDtoList: List<ProductDto>? = null
        threadAction {
            val response =
                service.requestProductWithCategory(
                    category = category,
                    size = count + cartItemIds.size,
                ).execute()
            if (response.isSuccessful && response.body() != null) {
                productDtoList = response.body()?.content
            }
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

    private fun threadAction(action: () -> Unit) {
        val latch = CountDownLatch(ACTION_COUNT)
        thread {
            try {
                action()
            } catch (e: Exception) {
                error(e)
            } finally {
                latch.countDown()
            }
        }
        latch.await()
    }

    companion object {
        private const val ACTION_COUNT = 1
    }
}
