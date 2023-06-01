package woowacourse.shopping.data.product

import woowacourse.shopping.data.entity.ProductEntity
import woowacourse.shopping.network.retrofit.ProductRetrofitService

class ProductRemoteSource(private val productService: ProductRetrofitService) : ProductDataSource {
    override fun findAll(): Result<List<ProductEntity>> {
        val response = productService.selectProducts().execute()
        return response.runCatching {
            if (code() != 200) throw Throwable(message())
            body() ?: throw Throwable(message())
        }
    }

    override fun findRanged(limit: Int, offset: Int): Result<List<ProductEntity>> {
        val response = productService.selectProducts().execute()
        return response.runCatching {
            if (code() != 200) throw Throwable(message())
            val body = body() ?: throw Throwable(message())
            body.slice(offset until body.size).take(limit)
        }
    }

    override fun countAll(): Result<Int> {
        return findAll().mapCatching {
            it.size
        }
    }

    override fun findById(id: Long): Result<ProductEntity> {
        val response = productService.selectProduct(id).execute()
        return response.runCatching {
            if (code() != 200) throw Throwable(message())
            body() ?: throw Throwable(message())
        }
    }
}
