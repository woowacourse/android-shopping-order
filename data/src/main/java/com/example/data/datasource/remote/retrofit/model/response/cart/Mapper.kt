package com.example.data.datasource.remote.retrofit.model.response.cart

import com.example.data.datasource.remote.retrofit.model.response.product.toProduct
import com.example.data.datasource.remote.retrofit.model.response.product.toProductContent
import com.example.domain.model.CartItem
import com.example.domain.model.CartPageAttribute
import com.example.domain.model.Quantity

fun CartContent.toCartItem() = CartItem(id, product.toProduct(), Quantity(quantity))

fun List<CartContent>.toCartItems(): List<CartItem> = map { it.toCartItem() }

fun CartItem.toCartContent() = CartContent(id, product.toProductContent(), quantity.count)

fun List<CartItem>.toCartContents() = map { it.toCartContent() }

fun CartResponse.toCartItems() = cartContent.map { it.toCartItem() }

fun CartResponse.toCartPage() = CartPageAttribute(totalPages, totalElements, first, last)
