package woowacourse.shopping.data.remote.api

import woowacourse.shopping.domain.model.product.Product

interface ProductServerApi {
    fun start()

    fun find(id: Long): Product

    fun findAll(): List<Product>

    fun getProducts(): List<Product>

    fun shutdown()
}
