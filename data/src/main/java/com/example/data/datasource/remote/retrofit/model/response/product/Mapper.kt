package com.example.data.datasource.remote.retrofit.model.response.product

import com.example.domain.model.Product

fun ProductContent.toProduct(): Product = Product(id, name, price, imageUrl, category)

fun Product.toProductContent(): ProductContent = ProductContent(id, name, price, imageUrl, category)

fun List<ProductContent>.toProduct(): List<Product> = map { it.toProduct() }

fun List<Product>.toProductContents(): List<ProductContent> = map { it.toProductContent() }

fun ProductResponse.toProductList(): List<Product> = this.productContent.toProduct()