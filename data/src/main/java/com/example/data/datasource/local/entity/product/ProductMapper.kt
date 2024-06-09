package com.example.data.datasource.local.entity.product

import com.example.domain.model.Product

fun ProductEntity.toProduct() = Product(productId, name, price, imageUrl, category)

fun List<ProductEntity>.toProducts(): List<Product> = map { it.toProduct() }

fun Product.toProductEntity() = ProductEntity(id, name, price, imageUrl, category)

fun List<Product>.toProductEntities(): List<ProductEntity> = map { it.toProductEntity() }
