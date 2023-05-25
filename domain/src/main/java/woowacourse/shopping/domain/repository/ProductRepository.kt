package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.Product

interface ProductRepository {
    fun getAll(callBack: (List<Product>) -> Unit)
    fun getProduct(id: Int, callBack: (Product?) -> Unit)
    fun getProductsByRange(mark: Int, rangeSize: Int, callBack: (List<Product>) -> Unit)
    fun getProductsById(ids: List<Int>, callBack: (List<Product>) -> Unit)
    fun isExistByMark(mark: Int, callBack: (Boolean) -> Unit)
}
