@file:Suppress("ktlint")

package woowacourse.shopping.fixture

import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

const val cartPage = """
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
            "quantity": 1,
            "product": {
                "id": 1,
                "name": "에어포스1",
                "price": 100000,
                "imageUrl": "https://kream-phinf.pstatic.net/MjAyNTA1MTNfMjI5/MDAxNzQ3MTA4MjUzOTg4.106G0-WfVU8g8ziNKgKJjc1_UXvF-2IatsA-Cz5mG1og.etXRFVPYqcs5J9HAfXpaHFPFHorGnZU4Nl7k4368rfog.PNG/a_090d2310040b4f9ca922f2498ae8ae3a.png?type=l",
                "category": "패션잡화"
            }
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
    "last": true,
    "totalElements": 2,
    "totalPages": 1,
    "size": 20,
    "number": 0,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "first": true,
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
                            1 -> json = cartPage
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
