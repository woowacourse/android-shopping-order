package woowacourse.shopping.data.service

import woowacourse.shopping.domain.model.Product

interface ProductService {
    fun getProductsByIds(ids: List<Long>): List<Product>?
}
