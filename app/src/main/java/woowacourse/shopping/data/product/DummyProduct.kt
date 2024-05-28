package woowacourse.shopping.data.product

import woowacourse.shopping.domain.model.Product

val products =
    listOf(
        Product(
            id = 0,
            imageUrl = "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/27/41/8412707034127_1.png",
            name = "마리오 그린올리브 300g",
            price = 4500,
            category = "food",
        ),
        Product(
            id = 1,
            imageUrl = "https://images.emarteveryday.co.kr/images/product/8801392067167/vSYMPCA3qqbLJjhv.png",
            name = "비비고 통새우 만두 200g",
            price = 5980,
            category = "food",
        ),
        Product(
            id = 3,
            imageUrl = "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/97/12/2500000351297_1.png",
            name = "스테비아 방울토마토 500g",
            price = 7890,
            category = "food",
        ),
        Product(
            id = 4,
            imageUrl = "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/85/00/8005121000085_1.png",
            name = "디벨라 스파게티면 500g",
            price = 4280,
            category = "food",
        ),
        Product(
            id = 5,
            imageUrl = "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/00/29/8809433792900_1.png",
            name = "생훈제연어 150g",
            price = 11900,
            category = "food",
        ),
        Product(
            id = 6,
            imageUrl = "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/32/30/8801392033032_1.png",
            name = "CJ 고메 소바바치킨 소이허니윙 300g",
            price = 9980,
            category = "food",
        ),
        Product(
            id = 7,
            imageUrl = "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/65/07/8003740660765_1.jpg",
            name = "아리기 바질패스토 190g",
            price = 4080,
            category = "food",
        ),
        Product(
            id = 8,
            imageUrl = "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/77/16/8809269671677_1.png",
            name = "피코크 초마짬뽕 1240g",
            price = 9980,
            category = "food",
        ),
    )

val dummyProducts =
    List(55) {
        products[it % products.size].copy(
            id = it,
            name = "$it ${products[it % products.size].name}",
        )
    }
