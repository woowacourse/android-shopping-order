package com.example.domain.repository

import com.example.domain.model.CartItem
import com.example.domain.model.Product
import com.example.domain.model.RecentProduct

interface RecentProductRepository {
    fun findLastOrNull(): RecentProduct?

    fun findRecentProducts(): List<RecentProduct>

    fun save(product: Product)

    fun getRecommendProducts(cartItems: List<CartItem>): List<Product>
}
