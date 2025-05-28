package woowacourse.shopping.data.repository

import woowacourse.shopping.product.catalog.ProductUiModel

interface CartProductRepository {
    fun insertCartProduct(cartProduct: ProductUiModel)

    fun deleteCartProduct(cartProduct: ProductUiModel)

    fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
        callback: (List<ProductUiModel>) -> Unit,
    )

    fun updateProduct(
        cartProduct: ProductUiModel,
        quantity: Int,
        callback: (ProductUiModel?) -> Unit,
    )

//    fun getProductQuantity(
//        id: Int,
//        callback: (Int?) -> Unit,
//    )

    fun getAllProductsSize(callback: (Int) -> Unit)

    fun getCartItemSize(callback: (Int) -> Unit)

    fun getTotalElements(callback: (Int) -> Unit)

    fun getCartProducts(
        totalElements: Int,
        callback: (List<ProductUiModel>) -> Unit,
    )
}
