package woowacourse.shopping.domain.usecase.order

fun interface LoadShippingFeeUseCase : () -> Long {

    companion object {
        private val instance: LoadShippingFeeUseCase = LoadShippingFeeUseCase { 3000 }
        fun instance(): LoadShippingFeeUseCase = instance
    }
}