@file:Suppress("ktlint")

package woowacourse.shopping.fixture

import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

const val cartPage1 = """
{
    "content": [
        {
            "id": 12410,
            "quantity": 1,
            "product": {
                "id": 2,
                "name": "에어포스2",
                "price": 100000,
                "imageUrl": "https://kream-phinf.pstatic.net/MjAyNTA1MTNfMjI5/MDAxNzQ3MTA4MjUzOTg4.106G0-WfVU8g8ziNKgKJjc1_UXvF-2IatsA-Cz5mG1og.etXRFVPYqcs5J9HAfXpaHFPFHorGnZU4Nl7k4368rfog.PNG/a_090d2310040b4f9ca922f2498ae8ae3a.png?type=l",
                "category": "패션잡화"
            }
        },
        {
            "id": 12411,
            "quantity": 2,
            "product": {
                "id": 1,
                "name": "에어포스1",
                "price": 100000,
                "imageUrl": "https://kream-phinf.pstatic.net/MjAyNTA1MTNfMjI5/MDAxNzQ3MTA4MjUzOTg4.106G0-WfVU8g8ziNKgKJjc1_UXvF-2IatsA-Cz5mG1og.etXRFVPYqcs5J9HAfXpaHFPFHorGnZU4Nl7k4368rfog.PNG/a_090d2310040b4f9ca922f2498ae8ae3a.png?type=l",
                "category": "패션잡화"
            }
        },
        {
            "id": 12878,
            "quantity": 2,
            "product": {
                "id": 3,
                "name": "에어포스3",
                "price": 100000,
                "imageUrl": "https://kream-phinf.pstatic.net/MjAyNTA1MTNfMjI5/MDAxNzQ3MTA4MjUzOTg4.106G0-WfVU8g8ziNKgKJjc1_UXvF-2IatsA-Cz5mG1og.etXRFVPYqcs5J9HAfXpaHFPFHorGnZU4Nl7k4368rfog.PNG/a_090d2310040b4f9ca922f2498ae8ae3a.png?type=l",
                "category": "패션잡화"
            }
        },
        {
            "id": 12879,
            "quantity": 2,
            "product": {
                "id": 4,
                "name": "달 무드등",
                "price": 28000,
                "imageUrl": "https://thumbnail6.coupangcdn.com/thumbnails/remote/492x492ex/image/vendor_inventory/794f/cecbea5bdc654a11ae02d28b4d1f4bd2a03a7389eb2b8cc4a45c1c9f7d9b.jpg",
                "category": "패션잡화"
            }
        },
        {
            "id": 12880,
            "quantity": 2,
            "product": {
                "id": 5,
                "name": "동물 양말",
                "price": 20000,
                "imageUrl": "https://m.cocosocks.com/web/product/medium/202503/940897aced51144109baa4d145def01f.jpg",
                "category": "패션잡화"
            }
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 5,
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
    "totalElements": 7,
    "totalPages": 2,
    "size": 5,
    "number": 0,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "first": true,
    "numberOfElements": 5,
    "empty": false
}
"""

const val cartPage2 = """
    {
    "content": [
        {
            "id": 12881,
            "quantity": 2,
            "product": {
                "id": 6,
                "name": "플라망고",
                "price": 8130,
                "imageUrl": "https://velog.velcdn.com/images/minsungje/post/c27c57cb-fcbb-4641-b72d-0e2030739ae7/image.jpg",
                "category": "식료품"
            }
        },
        {
            "id": 12882,
            "quantity": 2,
            "product": {
                "id": 7,
                "name": "메이통통이",
                "price": 11100000,
                "imageUrl": "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fb5H3cg%2FbtsMRVqcfYF%2FvbKfazkKNY7I5CGkF1Ye9k%2Fimg.png",
                "category": "식료품"
            }
        }
    ],
    "pageable": {
        "pageNumber": 1,
        "pageSize": 5,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "offset": 5,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalElements": 7,
    "totalPages": 2,
    "size": 5,
    "number": 1,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "first": false,
    "numberOfElements": 2,
    "empty": false
}
"""

const val cartCount = """
    {
    "quantity": 2
}
"""

class MockShoppingCartDispatcher(
    private val url: HttpUrl,
) {
    val dispatcher =
        object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when {
                    request.path?.startsWith("/cart-items") == true -> {
                        if (request.path?.startsWith("/cart-items/count") == true) {
                            return MockResponse()
                                .setHeader("Content-Type", "application/json")
                                .setResponseCode(200)
                                .setBody(cartCount)
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
                        val size = queryParams["size"]?.toIntOrNull() ?: 5

                        var json: String? = null
                        when (page) {
                            1 -> json = cartPage1
                            2 -> json = cartPage2
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
