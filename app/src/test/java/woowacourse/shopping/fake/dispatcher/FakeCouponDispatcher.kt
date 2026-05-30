package woowacourse.shopping.fake.dispatcher

import mockwebserver3.Dispatcher
import mockwebserver3.MockResponse
import mockwebserver3.RecordedRequest

class FakeCouponDispatcher : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse =
        when {
            request.method == "GET" && request.url.encodedPath.startsWith("/coupons") -> {
                MockResponse
                    .Builder()
                    .code(200)
                    .addHeader("Content-Type", "application/json")
                    .body(COUPONS_JSON)
                    .build()
            }
            else -> MockResponse.Builder().code(404).build()
        }

    companion object {
        private val COUPONS_JSON =
            """
            [
              {
                "discountType": "fixed",
                "id": 1,
                "code": "FIXED5000",
                "description": "5,000원 할인 쿠폰",
                "expirationDate": "2026-12-31",
                "discount": 5000,
                "minimumAmount": 100000
              },
              {
                "discountType": "buyXgetY",
                "id": 2,
                "code": "BUY2GET1",
                "description": "2개 구매 시 1개 무료",
                "expirationDate": "2026-12-31",                                                                                                                                    
                "buyQuantity": 2,
                "getQuantity": 1
              },
              {
                "discountType": "freeShipping",
                "id": 3,
                "code": "FREESHIP",
                "description": "무료 배송 쿠폰",
                "expirationDate": "2026-12-31",
                "minimumAmount": 50000
              },
              {
                "discountType": "percentage",
                "id": 4,
                "code": "PERCENT30",
                "description": "30% 할인 (점심시간)",
                "expirationDate": "2026-12-31",
                "discount": 30,
                "availableTime": {
                  "start": "12:00:00",
                  "end": "14:00:00"
                }
              }
            ]
            """.trimIndent()
    }
}
