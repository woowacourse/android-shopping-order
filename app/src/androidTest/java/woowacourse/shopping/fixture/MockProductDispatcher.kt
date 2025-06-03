@file:Suppress("ktlint")

package woowacourse.shopping.fixture

import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONArray
import org.json.JSONObject

const val page1 = """
    {
    "content": [
        {
            "id": 1,
            "name": "에어포스1에어포스1에어포스1에어포스1",
            "price": 100000,
            "imageUrl": "https://kream-phinf.pstatic.net/MjAyNTA1MTNfMjI5/MDAxNzQ3MTA4MjUzOTg4.106G0-WfVU8g8ziNKgKJjc1_UXvF-2IatsA-Cz5mG1og.etXRFVPYqcs5J9HAfXpaHFPFHorGnZU4Nl7k4368rfog.PNG/a_090d2310040b4f9ca922f2498ae8ae3a.png?type=l",
            "category": "패션잡화"
        },
        {
            "id": 2,
            "name": "에어포스2",
            "price": 100000,
            "imageUrl": "https://kream-phinf.pstatic.net/MjAyNTA1MTNfMjI5/MDAxNzQ3MTA4MjUzOTg4.106G0-WfVU8g8ziNKgKJjc1_UXvF-2IatsA-Cz5mG1og.etXRFVPYqcs5J9HAfXpaHFPFHorGnZU4Nl7k4368rfog.PNG/a_090d2310040b4f9ca922f2498ae8ae3a.png?type=l",
            "category": "패션잡화"
        },
        {
            "id": 3,
            "name": "에어포스3",
            "price": 100000,
            "imageUrl": "https://kream-phinf.pstatic.net/MjAyNTA1MTNfMjI5/MDAxNzQ3MTA4MjUzOTg4.106G0-WfVU8g8ziNKgKJjc1_UXvF-2IatsA-Cz5mG1og.etXRFVPYqcs5J9HAfXpaHFPFHorGnZU4Nl7k4368rfog.PNG/a_090d2310040b4f9ca922f2498ae8ae3a.png?type=l",
            "category": "패션잡화"
        },
        {
            "id": 4,
            "name": "달 무드등",
            "price": 28000,
            "imageUrl": "https://thumbnail6.coupangcdn.com/thumbnails/remote/492x492ex/image/vendor_inventory/794f/cecbea5bdc654a11ae02d28b4d1f4bd2a03a7389eb2b8cc4a45c1c9f7d9b.jpg",
            "category": "패션잡화"
        },
        {
            "id": 5,
            "name": "동물 양말",
            "price": 20000,
            "imageUrl": "https://m.cocosocks.com/web/product/medium/202503/940897aced51144109baa4d145def01f.jpg",
            "category": "패션잡화"
        },
        {
            "id": 6,
            "name": "플라망고",
            "price": 8130,
            "imageUrl": "https://velog.velcdn.com/images/minsungje/post/c27c57cb-fcbb-4641-b72d-0e2030739ae7/image.jpg",
            "category": "식료품"
        },
        {
            "id": 7,
            "name": "메이통통이",
            "price": 11100000,
            "imageUrl": "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fb5H3cg%2FbtsMRVqcfYF%2FvbKfazkKNY7I5CGkF1Ye9k%2Fimg.png",
            "category": "식료품"
        },
        {
            "id": 8,
            "name": "앵그리버드",
            "price": 50000,
            "imageUrl": "https://media.bunjang.co.kr/product/223522208_%7Bcnt%7D_1683581287_w%7Bres%7D.jpg",
            "category": "패션잡화"
        },
        {
            "id": 9,
            "name": "너에게난~ 해질녘 노을처럼~",
            "price": 200000,
            "imageUrl": "https://blog.kakaocdn.net/dn/qCz9R/btrmYEn7tZV/Uxh60wpS69qCFymU4WKOy0/img.jpg",
            "category": "패션잡화"
        },
        {
            "id": 22,
            "name": "앵버잠옷",
            "price": 200000,
            "imageUrl": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTZeoCnBP_VbQ4pLozKbZOIu6B0A9FB3gaeQA&s",
            "category": "패션잡화"
        },
        {
            "id": 23,
            "name": "리바이 아커만",
            "price": 60000000,
            "imageUrl": "https://image.zeta-ai.io/profile-image/793bf4d3-03de-4ac3-afe1-95be8a9bc62c/29cd5c72-f872-4dba-8be1-21ba51e4487f.jpeg?w=1080&q=90&f=webp",
            "category": "패션잡화"
        },
        {
            "id": 24,
            "name": "부리부리 원형 테이블",
            "price": 3210000,
            "imageUrl": "https://cafe24.poxo.com/ec01/dmswo9075/HOvhRhvOk+Cp2KY4JuusAqBst4wtnsfbyXcejHyxMmXKvNELh5kEAFzUfK9ehG6ogDMwTwYJTLHHXeYVBq809g==/_/web/product/big/202408/19deee5e9d060d80a4180e2b2ecb6ce8.jpg",
            "category": "패션잡화"
        },
        {
            "id": 25,
            "name": "얌샘김밥",
            "price": 5000,
            "imageUrl": "https://search.pstatic.net/common/?src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20171018_6%2F1508253136417Dlrjh_PNG%2FCdq22zpVpr92_XHROlHbxjJ0.png&type=sc960_832",
            "category": "식료품"
        },
        {
            "id": 26,
            "name": "기세",
            "price": 100,
            "imageUrl": "33",
            "category": "식료품"
        },
        {
            "id": 27,
            "name": "아바라",
            "price": 4800,
            "imageUrl": "https://image.ohousecdn.com/i/bucketplace-v2-development/uploads/cards/snapshots/171653801239329270.jpeg?w=256&h=366&c=c",
            "category": "식료품"
        },
        {
            "id": 28,
            "name": "아샷추",
            "price": 3800,
            "imageUrl": "https://d2afncas1tel3t.cloudfront.net/wp-content/uploads/2023/12/%EC%95%84%EC%83%B7%EC%B6%94%EC%95%84%EC%9D%B4%EC%8A%A4%ED%8B%B0%EC%83%B7%EC%B6%94%EC%B9%B4%EB%94%94%EC%B9%B4%ED%8E%98%EC%9D%B8_1.png",
            "category": "식료품"
        },
        {
            "id": 29,
            "name": "19×19×19 큐브",
            "price": 850000,
            "imageUrl": "https://i.namu.wiki/i/kQCwKHpwjePBTPXPTIizJSE0alohKKRlsGOJSrPhAdsODckkF05KNDV27xdydVqHLEdgM7yQu6NSUL-gE0t9SZH_cmaY8tMquJnfLQv5shH_pSdvsRc87hCcO5V3WBZrTwR23NYzoJJEoQIHWqAM4Q.webp",
            "category": "패션잡화"
        },
        {
            "id": 30,
            "name": "민초 피자",
            "price": 48000,
            "imageUrl": "https://www.esquirekorea.co.kr/resources_old/online/org_thumnail_image/eq/322f1c2e-fdd0-4b84-97ec-cfc8ee88c9d7.jpg",
            "category": "식료품"
        },
        {
            "id": 31,
            "name": "민초 치킨",
            "price": 47000,
            "imageUrl": "https://d2u3dcdbebyaiu.cloudfront.net/uploads/atch_img/218/20dddd283cca0cb01c9ac7285f20b704_res.jpeg",
            "category": "식료품"
        },
        {
            "id": 32,
            "name": "튀김 신발",
            "price": 800000,
            "imageUrl": "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ1bvoBgTtG0L-FBnZBsCOl5O-WcelpPH24IQ&s",
            "category": "패션 잡화"
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 20,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalElements": 79,
    "totalPages": 4,
    "size": 20,
    "number": 0,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "first": true,
    "numberOfElements": 20,
    "empty": false
}

"""

const val page2 = """
    {
    "content": [
        {
            "id": 33,
            "name": "iPhone 16 Pro Max 1TB",
            "price": 2500000,
            "imageUrl": "https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcQeMsjn-bl-bsreQfbsyA2l4EFwO5tsVDTYqUJY8GEctU6S1FkPyt7SxuALsS-9LZn2zXMvubxe5e0n_bEXY_JpTT_MsTfkQ1_MZuCD_FaFFzM5gM-YSxm3u246nBAM32NdyosLnQ&usqp=CAc",
            "category": "패션잡화"
        },
        {
            "id": 34,
            "name": "코카콜라 제로 1.5L",
            "price": 2100,
            "imageUrl": "https://sitem.ssgcdn.com/88/19/87/item/0000006871988_i1_750.jpg",
            "category": "식료품"
        },
        {
            "id": 35,
            "name": "패셔니스타 유담이",
            "price": 300000000,
            "imageUrl": "https://image.yes24.com/goods/84933797/XL",
            "category": "패션잡화"
        },
        {
            "id": 36,
            "name": "패셔니스타 유담이",
            "price": 300000000,
            "imageUrl": "https://image.yes24.com/goods/84933797/XL",
            "category": "패션잡화"
        },
        {
            "id": 37,
            "name": "패셔니스타 유담이",
            "price": 3000000,
            "imageUrl": "https://image.yes24.com/goods/84933797/XL",
            "category": "패션잡화"
        },
        {
            "id": 40,
            "name": "에리얼",
            "price": 123456789,
            "imageUrl": "https://i.namu.wiki/i/Flt-SDlty8ckiV5lxLr40M6HtBx0bwlGYs5FVYglOkQvCX0LeXtHOzE4qH6NfHci0DR9a7xr9OS3Bvlhi6JX0g.webp",
            "category": "식료품"
        },
        {
            "id": 42,
            "name": "프린세스 미용놀이",
            "price": 1010,
            "imageUrl": "https://pds.joongang.co.kr/news/component/htmlphoto_mmdata/202204/19/ed8eddd4-0edd-40ad-af7d-44a171577c92.jpg",
            "category": "패션잡화"
        },
        {
            "id": 43,
            "name": "모쏠 캥거루라 태어나서 처음으로 데이트하는 ㄱㅋ",
            "price": 99999999,
            "imageUrl": "https://lh3.googleusercontent.com/proxy/odVKuwM6Z8vZILjEfxFUSrCUC3PC6XTDHYV4lEz4mKobFYjHQqhIF9WvOdMMd6CNVVZnLvghnsF9kTSih-tMTAy4_ndA6Gs3r3dgQ_wAMgmjkZAA",
            "category": "식료품"
        },
        {
            "id": 57,
            "name": "후추",
            "price": 23000,
            "imageUrl": "https://i.namu.wiki/i/t4M8eo-01JpLUZLIrpTD5vqBnquZLvQrGZJ4Dl3lcXtbk5AOlyK2k3k-VOQQNhFyor-zEHGhlEn60FisBPIqjF8i2xRq10Dbc_Hgg5IbSGM0ROgmychWXYmJzU95XhFmpLMhgUyUGPMv7S9-6Jh6PQ.webp",
            "category": "패션잡화"
        },
        {
            "id": 58,
            "name": "1",
            "price": 100,
            "imageUrl": "이런 URL은 없겠지",
            "category": "식료품"
        },
        {
            "id": 60,
            "name": "3",
            "price": 30000000,
            "imageUrl": "이런 URL은 없겠지",
            "category": "식료품"
        },
        {
            "id": 65,
            "name": "8888",
            "price": 8,
            "imageUrl": "8",
            "category": "8"
        },
        {
            "id": 66,
            "name": "9",
            "price": 9,
            "imageUrl": "9",
            "category": "9"
        },
        {
            "id": 67,
            "name": "10",
            "price": 10,
            "imageUrl": "10",
            "category": "10"
        },
        {
            "id": 68,
            "name": "11",
            "price": 11,
            "imageUrl": "11",
            "category": "11"
        },
        {
            "id": 69,
            "name": "12",
            "price": 12,
            "imageUrl": "12",
            "category": "12"
        },
        {
            "id": 70,
            "name": "13",
            "price": 13,
            "imageUrl": "13",
            "category": "13"
        },
        {
            "id": 71,
            "name": "14",
            "price": 14,
            "imageUrl": "14",
            "category": "14"
        },
        {
            "id": 73,
            "name": "16",
            "price": 16,
            "imageUrl": "16",
            "category": "16"
        },
        {
            "id": 74,
            "name": "17",
            "price": 17,
            "imageUrl": "17",
            "category": "17"
        }
    ],
    "pageable": {
        "pageNumber": 1,
        "pageSize": 20,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "offset": 20,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalElements": 79,
    "totalPages": 4,
    "size": 20,
    "number": 1,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "first": false,
    "numberOfElements": 20,
    "empty": false
}
"""

const val productDetail = """
    {
    "id": 1,
    "name": "에어포스1",
    "price": 100000,
    "imageUrl": "https://kream-phinf.pstatic.net/MjAyNTA1MTNfMjI5/MDAxNzQ3MTA4MjUzOTg4.106G0-WfVU8g8ziNKgKJjc1_UXvF-2IatsA-Cz5mG1og.etXRFVPYqcs5J9HAfXpaHFPFHorGnZU4Nl7k4368rfog.PNG/a_090d2310040b4f9ca922f2498ae8ae3a.png?type=l",
    "category": "패션잡화"
}
"""

class MockProductDispatcher(
    private val url: HttpUrl,
) {
    private fun isProductPath(path: String): Boolean {
        val regex = Regex("^/products/\\d+$")
        return path.matches(regex)
    }

    private fun extractProductId(path: String): Long? {
        val regex = Regex("^/products/(\\d+)$")
        val matchResult = regex.find(path)
        return matchResult?.groups?.get(1)?.value?.toLongOrNull()
    }

    private fun findProductByIdInPage1(productId: Long): String? {
        try {
            val page1Json = JSONObject(page1)
            val contentArray: JSONArray = page1Json.getJSONArray("content")
            for (i in 0 until contentArray.length()) {
                val productObject = contentArray.getJSONObject(i)
                if (productObject.getLong("id") == productId) {
                    return productObject.toString() // 해당 상품 JSON 객체를 문자열로 반환
                }
            }
        } catch (e: Exception) {
            // JSON 파싱 오류 등 처리
            e.printStackTrace()
        }
        return null // 해당 ID의 상품을 찾지 못한 경우
    }

    val dispatcher =
        object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when {
                    request.path?.startsWith("/products") == true -> {
                        if (isProductPath(request.path!!)) {
                            val productId = extractProductId(request.path!!)
                            if (productId != null) {
                                val productJsonString = findProductByIdInPage1(productId)
                                return if (productJsonString != null) {
                                    MockResponse()
                                        .setHeader("Content-Type", "application/json")
                                        .setResponseCode(200)
                                        .setBody(productJsonString)
                                } else {
                                    // page1에 해당 ID의 상품이 없는 경우 (또는 다른 페이지도 검색하도록 확장 가능)
                                    MockResponse().setResponseCode(404)
                                        .setBody("Product not found in page1")
                                }
                            } else {
                                // ID를 제대로 추출하지 못한 경우 (isProductPath가 true이지만 ID 파싱 실패)
                                return MockResponse().setResponseCode(400)
                                    .setBody("Invalid product ID format")
                            }
                        }

                        val uri = url
                        val queryParams =
                            uri.query
                                ?.split("&")
                                ?.associate {
                                    val (key, value) = it.split("=")
                                    key to value
                                }.orEmpty()

                        val page = queryParams["page"]?.toIntOrNull() ?: 1
                        val size = queryParams["size"]?.toIntOrNull() ?: 20

                        var json: String? = null
                        when (page) {
                            1 -> json = page1
                            2 -> json = page2
                        }

                        json?.let {
                            MockResponse()
                                .setHeader("Content-Type", "application/json")
                                .setResponseCode(200)
                                .setBody(it)
                        } ?: run {
                            MockResponse().setResponseCode(404)
                        }
                    }

                    else -> {
                        MockResponse().setResponseCode(404)
                    }
                }
            }
        }
}
