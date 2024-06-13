package woowacourse.shopping.fixture.fake

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result
import kotlin.math.min

class FakeProductRepository(private val cartRepository: CartRepository = FakeCartRepository()):ProductRepository {
    override suspend fun getAllProducts(page: Int, size: Int): Result<List<Product>, DataError> {
        val startIndex = size * page
        return Result.Success(productStubs.subList(startIndex, min(startIndex + size, productStubs.size)))
    }

    override suspend fun getProductById(id: Long): Result<Product, DataError> {
        val product = productStubs.firstOrNull { it.id == id } ?: return Result.Error(DataError.NotFound)
        return Result.Success(product)
    }

    override suspend fun getAllRecommendProducts(category: String): Result<List<Product>, DataError> {
        val cartProductIds = when(val carts = cartRepository.getAllCartItems()) {
            is Result.Success -> carts.data.map { it.product.id }
            is Result.Error -> return Result.Error(carts.error)
        }
        val recommend = productStubs.filter { it.category == category }.filterNot { cartProductIds.contains(it.id) }
        return Result.Success(recommend)
    }

    companion object {
        val productStubs = (0..60).toList().map {
            when(it % 6) {
                0 -> Product(it.toLong(), "", "$it 번째 상품", it, "fashion")
                1 -> Product(it.toLong(), "", "$it 번째 상품", it, "beverage")
                2 -> Product(it.toLong(), "", "$it 번째 상품", it, "electronics")
                3 -> Product(it.toLong(), "", "$it 번째 상품", it, "kitchen")
                4 -> Product(it.toLong(), "", "$it 번째 상품", it, "fitness")
                5 -> Product(it.toLong(), "", "$it 번째 상품", it, "books")
                else ->  Product(it.toLong(), "", "$it 번째 상품", it, "books")
            }
        }
    }
}
