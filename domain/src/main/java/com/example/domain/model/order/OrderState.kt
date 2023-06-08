package com.example.domain.model.order

enum class OrderState(val value: String) {
    PENDING("Pending"),
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELED("Canceled")
}
