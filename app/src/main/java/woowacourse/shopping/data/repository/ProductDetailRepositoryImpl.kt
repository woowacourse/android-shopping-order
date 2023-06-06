package woowacourse.shopping.data.repository

import com.example.domain.model.Product
import com.example.domain.repository.ProductDetailRepository
import woowacourse.shopping.data.datasource.remote.producdetail.ProductDetailRemoteSource
import woowacourse.shopping.mapper.toDomain

class ProductDetailRepositoryImpl(
    private val productDetailRemoteSource: ProductDetailRemoteSource,
) : ProductDetailRepository {

    override fun getById(id: Long, callback: (Product) -> Unit) {
        productDetailRemoteSource.getById(id) {
            if (it.isSuccess) {
                val productDomain = it.getOrNull()?.toDomain()
                callback(productDomain ?: throw IllegalArgumentException())
            } else {
                throw IllegalArgumentException()
            }
        }
    }
}
