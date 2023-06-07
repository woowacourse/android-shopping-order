package com.example.domain.repository

import com.example.domain.model.BaseResponse
import com.example.domain.model.Product

interface ProductRepository {
    fun fetchFirstProducts(
        callBack: (BaseResponse<List<Product>>) -> Unit,
    )

    fun fetchNextProducts(
        lastProductId: Long,
        callBack: (BaseResponse<List<Product>>) -> Unit,
    )

    fun fetchProductById(
        productId: Long,
        callBack: (BaseResponse<Product>) -> Unit,
    )
}
