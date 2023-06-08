package com.example.domain.datasource

import com.example.domain.model.Order
import com.example.domain.model.OrderStatus
import com.example.domain.model.Price

val orderDataSource: List<Order> = List(45) { id ->
    Order(
        orderId = id,
        payAmount = Price(1000),
        orderAt = "2023-05-29",
        orderStatus = OrderStatus.PENDING,
        productName = "유명산지 고당도사과 $id",
        productImageUrl = "https://product-image.kurly.com/cdn-cgi/image/quality=85,width=676/product/image/b573ba85-9bfa-433b-bafc-3356b081440b.jpg",
        totalProductCount = 5,
    )
}
