package woowacourse.shopping.data.product

import woowacourse.shopping.data.product.source.NetworkProduct

fun NetworkProduct.toExternal() = Product(id, imageUrl, name, price)
