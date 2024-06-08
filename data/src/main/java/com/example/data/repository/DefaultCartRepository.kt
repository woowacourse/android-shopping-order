package com.example.data.repository

import com.example.domain.datasource.CartDataSource
import com.example.domain.datasource.DataResponse
import com.example.domain.model.CartItem
import com.example.domain.model.Quantity
import com.example.domain.repository.CartRepository

class DefaultCartRepository(
    private val dataSource: CartDataSource,
) : CartRepository {
    override fun findAll(): DataResponse<List<CartItem>> = dataSource.findAll()

    override fun increaseQuantity(productId: Int) = dataSource.increaseQuantity(productId)

    override fun decreaseQuantity(productId: Int) = dataSource.decreaseQuantity(productId)

    override fun changeQuantity(
        productId: Int,
        quantity: Quantity,
    ) = dataSource.changeQuantity(productId, quantity)

    override fun deleteCartItem(productId: Int) = dataSource.deleteByProductIdCartItem(productId)

    override fun find(productId: Int): DataResponse<CartItem> = dataSource.findByProductId(productId)

    override fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<CartItem>> = dataSource.findRange(page, pageSize)

    override fun totalCartItemCount(): DataResponse<Int> = dataSource.totalCartItemCount()
}
