package woowacourse.shopping.data.mockserver

import woowacourse.shopping.domain.model.Product

interface ProductServerApi {
    fun start()

    fun find(id: Long): Product

    fun findAll(): List<Product>

    fun getProducts(): List<Product>

    fun shutdown()
}
