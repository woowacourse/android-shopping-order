package woowacourse.shopping.util

import woowacourse.shopping.data.local.database.RecentProductEntity
import java.time.LocalDateTime

fun getSingleRecentProductFixtureItem(
    productId: Int,
    productName: String,
    imageUrl: String,
    category: String,
    dateTime: String = LocalDateTime.now().toString(),
): RecentProductEntity =
    RecentProductEntity(
        productId = productId,
        productName = productName,
        imageUrl = imageUrl,
        dateTime = dateTime,
        category = category,
    )

fun getMultipleRecentProductFixtureItems(
    count: Int,
    productNamePrefix: String = "사과",
    imageUrlPrefix: String = "image",
    category: String = "fruit",
): List<RecentProductEntity> =
    List(count) {
        getSingleRecentProductFixtureItem(
            productId = it + 1,
            productName = "$productNamePrefix${it + 1}",
            imageUrl = "$imageUrlPrefix${it + 1}",
            category = category,
        )
    }
