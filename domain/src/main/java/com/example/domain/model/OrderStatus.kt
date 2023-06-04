package com.example.domain.model

enum class OrderStatus(val value: String) {
    PENDING("Pending"),
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELLED("Canceled")
    ;

    companion object {
        fun of(value: String): OrderStatus {
            return OrderStatus.values().first { it.value == value }
        }
    }
}
