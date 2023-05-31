package woowacourse.shopping.domain.model

enum class Operator {
    INCREASE {
        override fun operate(cartProduct: CartProduct): CartProduct {
            return cartProduct.increase()
        }
    },
    DECREASE {
        override fun operate(cartProduct: CartProduct): CartProduct {
            return cartProduct.decrease()
        }
    }, ;

    abstract fun operate(cartProduct: CartProduct): CartProduct
}
