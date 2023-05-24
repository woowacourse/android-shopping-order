package woowacourse.shopping.util.inject

import woowacourse.shopping.data.datasource.cart.CartDataSource
import woowacourse.shopping.data.datasource.product.ProductDataSource
import woowacourse.shopping.data.datasource.recentproduct.RecentProductDataSource
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository

fun inject(productService: ProductService): ProductRepository =
    ProductRepositoryImpl(productService)

fun inject(localDataSource: RecentProductDataSource.Local): RecentProductRepository =
    RecentProductRepositoryImpl(localDataSource)

fun inject(localDataSource: CartDataSource.Local): CartRepository =
    CartRepositoryImpl(localDataSource)
