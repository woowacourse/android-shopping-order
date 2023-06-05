package woowacourse.shopping.data.respository.product

import com.example.domain.cart.CartProduct
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.respository.product.source.remote.ProductRemoteDataSource

class ProductRepositoryImpl(
    private val productRemoteDataSource: ProductRemoteDataSource,
) : ProductRepository {
    override fun loadDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartProduct>) -> Unit,
    ) {
        productRemoteDataSource.requestDatas(onFailure) { cartRemoteEntities ->
            onSuccess(cartRemoteEntities.map { it.toDomain() })
        }
    }

    override fun loadDataById(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (product: CartProduct) -> Unit,
    ) {
        productRemoteDataSource.requestData(productId, onFailure) { cartRemoteEntity ->
            onSuccess(cartRemoteEntity.toDomain())
        }
    }
}
