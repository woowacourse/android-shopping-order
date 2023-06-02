package woowacourse.shopping.data.product

import woowacourse.shopping.data.entity.ProductEntity
import woowacourse.shopping.network.retrofit.ProductRetrofitService

class ProductRemoteSource(private val productService: ProductRetrofitService) : ProductDataSource {
    override fun findAll(): Result<List<ProductEntity>> {
        return runCatching {
            val response = productService.selectProducts().execute()
            if (response.code() != 200) throw Throwable(response.message())
            response.body() ?: throw Throwable(response.message())
        }
    }

    override fun findRanged(limit: Int, offset: Int): Result<List<ProductEntity>> {
        return runCatching {
            val response = productService.selectProducts().execute()
            if (response.code() != 200) throw Throwable(response.message())
            val body = response.body() ?: throw Throwable(response.message())
            body.slice(offset until body.size).take(limit)
        }
    }

    override fun countAll(): Result<Int> {
        return findAll().mapCatching {
            it.size
        }
    }

    override fun findById(id: Long): Result<ProductEntity> {
        return runCatching {
            val response = productService.selectProduct(id).execute()
            if (response.code() != 200) throw Throwable(response.message())
            response.body() ?: throw Throwable(response.message())
        }
    }
}
