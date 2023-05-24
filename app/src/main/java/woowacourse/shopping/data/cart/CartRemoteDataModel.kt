package woowacourse.shopping.data.cart

import woowacourse.shopping.data.product.ProductDataModel

data class CartRemoteDataModel(val product: ProductDataModel, var count: Int)
