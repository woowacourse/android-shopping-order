package woowacourse.shopping.data.storage

import woowacourse.shopping.data.network.entity.ProductEntity
import woowacourse.shopping.data.network.entity.ProductPageEntity

@Suppress("ktlint:standard:max-line-length")
object ProductStorage {
    private val productEntities = mutableMapOf<Long, ProductEntity>()
    private val ProductEntitiesValues get() = productEntities.values.toList()

    init {
        initialize()
    }

    private fun initialize() {
        listOf(
            ProductEntity(
                1L,
                "마리오 그린올리브 300g",
                "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/27/41/8412707034127_1.png",
                3980,
                1,
            ),
            ProductEntity(
                id = 2L,
                name = "[태우한우] 1+ 한우 안심 스테이크 200g (냉장)",
                imgUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/c1ea8fff-29d9-4e12-b2f1-667d76e2bdc9.jpeg",
                price = 2000,
                quantity = 10,
            ),
            ProductEntity(
                3L,
                "스테비아 방울토마토 500g",
                "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/97/12/2500000351297_1.png",
                8986,
                10,
            ),
            ProductEntity(
                4L,
                "디벨라 스파게티면 500g",
                "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/85/00/8005121000085_1.png",
                1980,
                10,
            ),
            ProductEntity(
                5L,
                "생훈제연어 150g",
                "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/00/29/8809433792900_1.png",
                8981,
                12,
            ),
            ProductEntity(
                6L,
                "CJ 고메 소바바치킨 소이허니윙 300g",
                "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/32/30/8801392033032_1.png",
                8982,
                15,
            ),
            ProductEntity(
                7L,
                "아리기 바질패스토 190g",
                "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/65/07/8003740660765_1.jpg",
                8980,
                25,
            ),
            ProductEntity(
                8L,
                "피코크 초마짬뽕 1240g",
                "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/77/16/8809269671677_1.png",
                9980,
                35,
            ),
            ProductEntity(
                9L,
                "[병천아우내] 모듬순대",
                "https://images.emarteveryday.co.kr/images/app/webapps/evd_web2/share/SKU/mall/77/16/8809269671677_1.png",
                1190,
                13,
            ),
            ProductEntity(
                10L,
                "[빙그래] 요맘때 파인트 710mL 3종 (택1)",
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/73061aab-a2e2-443a-b0f9-f19b7110045e.jpg",
                5000,
                28,
            ),
            ProductEntity(
                11L,
                "[애슐리] 크런치즈엣지 포테이토피자 495g",
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/23efcafe-0765-478f-afe9-f9af7bb9b7df.jpg",
                1090,
                22,
            ),
            ProductEntity(
                12L,
                "치밥하기 좋은 순살 바베큐치킨",
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/f864b361-85da-4482-aec8-909397caac4e.jpg",
                1399,
                17,
            ),
            ProductEntity(
                13L,
                "[이연복의 목란] 짜장면 2인분",
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/90256eb2-b02f-493a-ab7a-29a8724254e4.jpeg",
                9980,
                16,
            ),
            ProductEntity(
                14L,
                "[콜린스 다이닝] 마르게리타 미트볼",
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/f92fa98a-524c-431e-a974-e32fcc8fe2ca.jpg",
                1140,
                12,
            ),
            ProductEntity(
                15L,
                "[투다리] 푸짐한 김치어묵 우동전골",
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/1568a441-bb45-4732-8a69-c599aa8ecfbf.jpg",
                1390,
                7,
            ),
            ProductEntity(
                16L,
                "[투다리] 한우대창나베",
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/997b370e-16a5-473d-9586-4dc97f1530aa.jpg",
                1720,
                9,
            ),
            ProductEntity(
                17L,
                "[런던베이글뮤지엄] 베이글 6개 & 크림치즈 3개 세트",
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/3c68d05b-d392-4a38-8637-a25068220fa4.jpg",
                4200,
                1,
            ),
            ProductEntity(
                18L,
                "[투다리] 오리지널 알탕",
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/ace6bb54-2434-4ca0-86ab-5bea178f5669.jpg",
                1490,
                1,
            ),
            ProductEntity(
                19L,
                "[소반옥] 왕갈비탕 1kg",
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/860123d3-be82-4c90-ae47-0b56e2869eca.jpg",
                1190,
                4,
            ),
            ProductEntity(
                20L,
                "[금룡각] 마라탕",
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/0634a5e8-51e8-4dcd-8b82-6f6237e8c261.jpg",
                1590,
                3,
            ),
            ProductEntity(
                21L,
                "[모현상회] 대광어회 150g (냉장)",
                "https://img-cf.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/shop/data/goods/160369196760l0.jpg",
                1690,
                5,
            ),
            ProductEntity(
                id = 22,
                name = "[사미헌] 우거지 갈비탕",
                imgUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/fe13821c-60a1-4c99-bdc7-c360ec445ea0.jpeg",
                price = 9900,
                quantity = 1,
            ),
            ProductEntity(
                id = 23,
                name = "[최현석의 쵸이닷] 파스타 인기 메뉴 12종 (택1)",
                imgUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/4b873e8d-b161-45ea-92ad-95d01cc8a9fa.jpg",
                price = 7900,
                quantity = 1,
            ),
            ProductEntity(
                id = 24,
                name = "[일상식탁] 부산식 얼큰 낙곱새",
                imgUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/39b48630-0359-4f08-aea3-8193aea1fc52.jpg",
                price = 1890,
                quantity = 10,
            ),
            ProductEntity(
                id = 25,
                name = "[본죽] 메추리알 장조림",
                imgUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/b4f210d2-b6b0-4328-9357-c96a875d5b29.jpg",
                price = 1090,
                quantity = 10,
            ),
            ProductEntity(
                id = 26,
                name = "[미트클레버] 대구막창",
                imgUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/1b2fd7b6-32ad-4c9f-9303-6894b2a8bfb9.jpeg?v=0531",
                price = 1990,
                quantity = 12,
            ),
            ProductEntity(
                id = 27,
                name = "[배나무골] 연잎 삼겹살 (냉장)",
                imgUrl = "https://img-cf.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/shop/data/goods/1600669626616l0.jpg",
                price = 1290,
                quantity = 10,
            ),
            ProductEntity(
                id = 28,
                name = "[하루한킷] 송탄식 부대찌개",
                imgUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/a872cc78-6dd5-4c7e-9a59-f84128fada19.jpg?v=0531",
                price = 1590,
                quantity = 12,
            ),
            ProductEntity(
                id = 29,
                name = "[성수동 팩피 : FAGP] 감바스 파스타",
                imgUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/f16d346d-1a89-4620-8c18-9d29e7666971.jpg",
                price = 9200,
                quantity = 12,
            ),
            ProductEntity(
                id = 30,
                name = "[크리스피크림도넛] 오리지널 글레이즈드 (9개입)",
                imgUrl = "https://img-cf.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/shop/data/goods/164870681737l0.jpg",
                price = 1620,
                quantity = 10,
            ),
            ProductEntity(
                id = 31,
                name = "[궁] 고추장 제육 돈불고기 600g",
                imgUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/79a25a83-529b-4be2-b67f-e62bdc235f9a.jpg",
                price = 9900,
                quantity = 13,
            ),
            ProductEntity(
                id = 32,
                name = "[부산 상국이네] 떡볶이 (2~3인분)",
                imgUrl = "https://img-cf.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/shop/data/goods/1646963339667l0.jpg",
                price = 8400,
                quantity = 111,
            ),
            ProductEntity(
                id = 33,
                name = "[최현석의 쵸이닷] 트러플 크림 뇨끼",
                imgUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/1d6a8e22-3227-4974-a932-93bbb244e49e.jpg",
                price = 7900,
                quantity = 10,
            ),
            ProductEntity(
                id = 34,
                name = "[골라담기] 오뚜기 라면 6종 균일가 (택3)",
                imgUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/d7c9fb3c-81bc-4f7b-bcf9-dea831cef649.jpg",
                price = 4180,
                quantity = 8,
            ),
            ProductEntity(
                id = 35,
                name = "냉동 유기농 블루베리 700g (미국산)",
                imgUrl = "https://img-cf.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/shop/data/goods/1653037727503l0.jpeg",
                price = 2290,
                quantity = 9,
            ),
            ProductEntity(
                id = 36,
                name = "[애슐리] 홈스토랑 볶음밥 6종 (4개입) (택1)",
                imgUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/26376544-1943-4773-8665-7f7a1fa1dfb5.jpg",
                price = 1190,
                quantity = 10,
            ),
            ProductEntity(
                id = 37,
                name = "[더건강한] 닭가슴살 2종 (100g*4) (택1)",
                imgUrl = "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/3d8a3861-f778-44ef-bff1-d665be4d8f19.jpg",
                price = 1298,
                quantity = 10,
            ),
        ).forEach {
            productEntities[it.id] = it
        }
    }

    operator fun get(id: Long): ProductEntity = productEntities[id] ?: throw IllegalArgumentException()

    fun getProductsById(productIds: List<Long>): List<ProductEntity> {
        return productIds.mapNotNull { productEntities[it] }
    }

    fun singlePage(
        fromIndex: Int,
        toIndex: Int,
    ): ProductPageEntity {
        val endIndex = minOf(toIndex, productEntities.size)

        if (fromIndex >= productEntities.size || fromIndex < 0) {
            return ProductPageEntity(emptyList(), false)
        }

        val result = ProductEntitiesValues.subList(fromIndex, endIndex)
        val hasNextPage = endIndex < productEntities.size

        return ProductPageEntity(result, hasNextPage)
    }
}
