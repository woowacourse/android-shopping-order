package woowacourse.shopping.presentation.cart.viewholder

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemCartBinding
import woowacourse.shopping.presentation.cart.CartContract
import woowacourse.shopping.presentation.common.CounterListener
import woowacourse.shopping.presentation.model.CartProductInfoModel

class CartItemViewHolder(
    private val binding: ItemCartBinding,
    private val presenter: CartContract.Presenter,
    private val updateProductPrice: (TextView, CartProductInfoModel) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var cartProductModel: CartProductInfoModel
    private val counterListener = object : CounterListener {
        override fun onPlus(count: Int) {
            updateProductCount(count)
        }

        override fun onMinus(count: Int) {
            updateProductCount(count)
        }
    }

    init {
        deleteButtonClick()
    }

    private fun updateProductCount(count: Int) {
        presenter.updateProductCount(cartProductModel, count)
    }

    fun bind(item: CartProductInfoModel) {
        cartProductModel = item
        binding.cartProduct = cartProductModel
        updateProductPrice(binding.textCartProductPrice, cartProductModel)
        setUpCounterView()
        checkBoxChange()
    }

    private fun setUpCounterView() {
        binding.counterCartProduct.setUpView(
            counterListener,
            initCount = cartProductModel.count,
            minimumCount = 1,
        )
    }

    private fun deleteButtonClick() {
        binding.imageCartDelete.setOnClickListener {
            presenter.deleteProductItem(cartProductModel)
        }
    }

    private fun checkBoxChange() {
        binding.checkboxProductCart.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                presenter.addProductInOrder(cartProductModel)
            } else {
                presenter.deleteProductInOrder(cartProductModel)
            }
        }
    }
}
