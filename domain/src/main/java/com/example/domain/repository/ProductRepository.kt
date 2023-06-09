package com.example.domain.repository

import com.example.domain.model.ProductItem

interface ProductRepository {
    fun getMoreProducts(limit: Int, scrollCount: Int, callback: (List<ProductItem>) -> Unit)
}
