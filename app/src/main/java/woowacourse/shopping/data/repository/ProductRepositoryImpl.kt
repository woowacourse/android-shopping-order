package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dataSource.ProductDataSource
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.ProductRepository

class ProductRepositoryImpl(
    private val remoteDatabase: ProductDataSource,
) : ProductRepository {
    override fun getAll(
        onSuccess: (List<Product>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        remoteDatabase.getAll(
            {
                onSuccess(it.map { productDto -> productDto.toDomain() })
            },
            {
            },
        )
    }

    override fun findById(id: Int, onSuccess: (Product) -> Unit, onFailure: (Exception) -> Unit) {
        remoteDatabase.findById(
            id,
            {
                onSuccess(it.toDomain())
            },
            {},
        )
    }
}
