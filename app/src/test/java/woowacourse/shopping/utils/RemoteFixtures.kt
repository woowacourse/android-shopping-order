package woowacourse.shopping.utils

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun getRetrofit(mockWebServer: MockWebServer): Retrofit =
    Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

const val PRODUCT_RESPONSE =
    """
        {
          "totalPages": 0,
          "totalElements": 5,
          "sort": {
            "sorted": true,
            "unsorted": true,
            "empty": true
          },
          "first": true,
          "last": true,
          "pageable": {
            "sort": {
              "sorted": true,
              "unsorted": true,
              "empty": true
            },
            "pageNumber": 0,
            "pageSize": 5,
            "paged": true,
            "unpaged": true,
            "offset": 0
          },
          "number": 0,
          "numberOfElements": 0,
          "size": 0,
          "content": [
            {
              "id": 1,
              "name": "string",
              "price": 0,
              "imageUrl": "string",
              "category": "vegetable"
            },    
            {
              "id": 2,
              "name": "string",
              "price": 0,
              "imageUrl": "string",
              "category": "fruit"
            },
            {
              "id": 3,
              "name": "string",
              "price": 0,
              "imageUrl": "string",
              "category": "fruit"
            },
            {
              "id": 4,
              "name": "string",
              "price": 1000,
              "imageUrl": "string",
              "category": "vegetable"
            },
            {
              "id": 5,
              "name": "string",
              "price": 1000,
              "imageUrl": "string",
              "category": "fruit"
            }
          ],
          "empty": true
        }
    """

const val SINGLE_PRODUCT_RESPONSE = """
    {
      "id": 1,
      "name": "string",
      "price": 0,
      "imageUrl": "string",
      "category": "vegetable"
    }
"""

const val COUPONS_RESPONSE = """
    [
  {
    "id": 1,
    "code": "FIXED5000",
    "description": "5,000원 할인 쿠폰",
    "expirationDate": "2024-11-30",
    "discount": 5000,
    "minimumAmount": 100000,
    "discountType": "fixed"
  },
  {
    "id": 2,
    "code": "BOGO",
    "description": "2개 구매 시 1개 무료 쿠폰",
    "expirationDate": "2024-05-30",
    "buyQuantity": 2,
    "getQuantity": 1,
    "discountType": "buyXgetY"
  },
  {
    "id": 3,
    "code": "FREESHIPPING",
    "description": "5만원 이상 구매 시 무료 배송 쿠폰",
    "expirationDate": "2024-08-31",
    "minimumAmount": 50000,
    "discountType": "freeShipping"
  },
  {
    "id": 4,
    "code": "MIRACLESALE",
    "description": "미라클모닝 30% 할인 쿠폰",
    "expirationDate": "2024-07-31",
    "discount": 30,
    "availableTime": {
      "start": "04:00:00",
      "end": "07:00:00"
    },
    "discountType": "percentage"
  }
]
"""

const val CART_RESPONSE = """
{
  "content": [
    {
      "id": 16439,
      "quantity": 2,
      "product": {
        "id": 2,
        "name": "나이키",
        "price": 1000,
        "imageUrl": "https://static.nike.com/a/images/c_limit,w_592,f_auto/t_product_v1/a28864e3-de02-48bb-b861-9c592bc9a68b/%EB%B6%81-1-ep-%EB%86%8D%EA%B5%AC%ED%99%94-UOp6QQvg.png",
        "category": "fashion"
      }
    },
    {
      "id": 16440,
      "quantity": 1,
      "product": {
        "id": 3,
        "name": "아디다스",
        "price": 2000,
        "imageUrl": "https://sitem.ssgcdn.com/74/25/04/item/1000373042574_i1_750.jpg",
        "category": "fashion"
      }
    },
    {
      "id": 16441,
      "quantity": 2,
      "product": {
        "id": 10,
        "name": "퓨마",
        "price": 10000,
        "imageUrl": "https://sitem.ssgcdn.com/47/78/22/item/1000031227847_i1_750.jpg",
        "category": "fashion"
      }
    },
    {
      "id": 16442,
      "quantity": 2,
      "product": {
        "id": 11,
        "name": "리복",
        "price": 20000,
        "imageUrl": "https://image.msscdn.net/images/goods_img/20221031/2909092/2909092_6_500.jpg",
        "category": "fashion"
      }
    },
    {
      "id": 16443,
      "quantity": 3,
      "product": {
        "id": 12,
        "name": "컨버스",
        "price": 20000,
        "imageUrl": "https://sitem.ssgcdn.com/65/73/69/item/1000163697365_i1_750.jpg",
        "category": "fashion"
      }
    }
  ],
  "pageable": {
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalPages": 1,
  "totalElements": 5,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "first": true,
  "number": 0,
  "numberOfElements": 5,
  "size": 5,
  "empty": false
}
"""
