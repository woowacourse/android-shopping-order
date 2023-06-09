package woowacourse.shopping.model.mapper

import com.example.domain.product.Product
import com.example.domain.product.recent.RecentProduct
import woowacourse.shopping.model.ProductState
import java.time.LocalDateTime

fun Product.toUi(): ProductState {
    return ProductState(id, imageUrl, name, price)
}

fun Product.toRecentProduct(nowDateTime: LocalDateTime): RecentProduct {
    return RecentProduct(id, imageUrl, name, price, nowDateTime)
}

fun ProductState.toDomain(): Product {
    return Product(id, imageUrl, name, price)
}
