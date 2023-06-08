package woowacourse.shopping.model.mapper

import com.example.domain.product.Product
import com.example.domain.product.recent.RecentProduct
import woowacourse.shopping.model.RecentProductState

fun RecentProduct.toUi(): RecentProductState {
    return RecentProductState(productId, productImageUrl, productPrice, productName)
}

fun RecentProductState.toProduct(): Product {
    return Product(productId, productImageUrl, productName, productPrice)
}
