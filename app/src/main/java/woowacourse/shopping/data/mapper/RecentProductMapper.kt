package woowacourse.shopping.data.mapper

import com.example.domain.RecentProduct
import woowacourse.shopping.data.model.RecentProductEntity

fun RecentProduct.toEntity(): RecentProductEntity =
    RecentProductEntity(id, productId)

fun RecentProductEntity.toDomain(): RecentProduct =
    RecentProduct(id, productId)
