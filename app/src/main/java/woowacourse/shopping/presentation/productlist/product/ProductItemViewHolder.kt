package woowacourse.shopping.presentation.productlist.product

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.databinding.ItemProductBinding
import woowacourse.shopping.presentation.common.CounterListener
import woowacourse.shopping.presentation.model.CartProductInfoModel
import woowacourse.shopping.presentation.productdetail.ProductDetailActivity
import woowacourse.shopping.presentation.productlist.ProductListContract

class ProductItemViewHolder(
    private val binding: ItemProductBinding,
    private val presenter: ProductListContract.Presenter,
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var cartProductModel: CartProductInfoModel
    private val counterListener = object : CounterListener {
        override fun onPlus(count: Int) {
            presenter.updateCartItemQuantity(cartProductModel, count)
        }

        override fun onMinus(count: Int) {
            presenter.updateCartItemQuantity(cartProductModel, count)
        }
    }

    init {
        itemViewClick()
    }

    fun bind(item: CartProductInfoModel) {
        cartProductModel = item
        binding.cartProductModel = cartProductModel
        setUpCounterView()
        setUpAddButtonView()
    }

    private fun itemViewClick() {
        itemView.setOnClickListener {
            showProductDetail()
        }
    }

    private fun showProductDetail() {
        itemView.context.startActivity(
            ProductDetailActivity.getIntent(
                itemView.context,
                cartProductModel.productModel,
            ),
        )
    }

    private fun setUpCounterView() {
        binding.counterProductList.setUpView(
            counterListener = counterListener,
            initCount = cartProductModel.count,
            minimumCount = 0,
        )
    }

    private fun setUpAddButtonView() {
        with(binding.buttonProductListAddCart) {
            setOnClickListener { addButtonClick() }
            visibility = addButtonVisibility()
        }
    }

    private fun addButtonClick() {
        presenter.addCartItem(cartProductModel)
        presenter.refreshProductItems()
    }

    private fun addButtonVisibility(): Int {
        return if (cartProductModel.count == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}
