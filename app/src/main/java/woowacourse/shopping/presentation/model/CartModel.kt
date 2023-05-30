package woowacourse.shopping.presentation.model

data class CartModel(
    val id: Long,
    val product: ProductModel,
    val count: Int,
    var checked: Boolean,
) {
    companion object {
        private const val TEMPORARY_ID = 900000
        private const val DEFAULT_COUNT = 0

        fun getNoHasCountCartProduct(product: ProductModel): CartModel {
            return CartModel(
                TEMPORARY_ID + product.id,
                product,
                DEFAULT_COUNT,
                checked = true
            )
        }
    }
}
