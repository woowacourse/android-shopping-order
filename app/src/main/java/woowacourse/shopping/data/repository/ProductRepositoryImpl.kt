package woowacourse.shopping.data.repository

import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository
import woowacourse.shopping.data.datasource.remote.product.ProductRemoteDataSource
import woowacourse.shopping.mapper.toDomain

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override fun getMoreProducts(limit: Int, scrollCount: Int): Result<List<Product>> {
        val result = productRemoteDataSource.getSubListProducts(limit, scrollCount)
        return if (result.isSuccess) {
            val productsDomain = result.getOrNull()?.map { productDto -> productDto.toDomain() }
            Result.success(productsDomain ?: emptyList())
        } else {
            Result.failure(Throwable(result.exceptionOrNull()?.message))
        }
    }
}
