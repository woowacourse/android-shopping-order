package woowacourse.shopping

import woowacourse.shopping.data.local.mapper.toEntity
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct

val cartProducts =
    List(51) { id ->
        CartProduct(
            productId = id.toLong(),
            name = "$id",
            imgUrl = "https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg",
            price = 10000,
            quantity = 1,
        )
    }

val cartProduct: CartProduct = cartProducts.first()

val recentProducts =
    List(10) { id ->
        RecentProduct(
            productId = id.toLong(),
            name = "$id",
            imgUrl = "www.naver.com",
            price = 10000,
            createdAt = System.currentTimeMillis(),
            category = "fashion",
            cartId = id.toLong(),
            quantity = 0,
        )
    }

val recentProduct: RecentProduct = recentProducts.first()

val recentProductEntities = recentProducts.map { it.toEntity() }
val recentProductEntity = recentProduct.toEntity()
