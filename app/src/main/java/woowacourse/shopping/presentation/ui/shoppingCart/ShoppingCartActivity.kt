package woowacourse.shopping.presentation.ui.shoppingCart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.defaultRepository.DefaultChargeRepository
import woowacourse.shopping.data.defaultRepository.DefaultOrderRepository
import woowacourse.shopping.data.defaultRepository.DefaultShoppingCartRepository
import woowacourse.shopping.data.remote.shoppingCart.ShoppingCartRemoteDataSource
import woowacourse.shopping.databinding.ActivityShoppingCartBinding
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.presentation.ui.order.OrderDetailActivity
import woowacourse.shopping.presentation.ui.productDetail.ProductDetailActivity
import woowacourse.shopping.presentation.ui.shoppingCart.adapter.ShoppingCartAdapter

class ShoppingCartActivity :
    AppCompatActivity(),
    ShoppingCartContract.View {
    private lateinit var binding: ActivityShoppingCartBinding
    override lateinit var presenter: ShoppingCartContract.Presenter
    private val shoppingCartAdapter by lazy {
        ShoppingCartAdapter(ShoppingCartClickListenerImpl(presenter))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = initPresenter()
        initClickListeners()
        binding.listShoppingCart.adapter = shoppingCartAdapter
    }

    override fun onStart() {
        super.onStart()
        presenter.fetchChange()
        // registerForActivityResult를 사용했다면 더 좋았겠지만,, 시간상의 문제로 이렇게 진행했습니다,,
    }

    private fun initPresenter(): ShoppingCartPresenter {
        return ShoppingCartPresenter(
            this,
            DefaultShoppingCartRepository(ShoppingCartRemoteDataSource()),
            DefaultChargeRepository(),
            DefaultOrderRepository(),
        )
    }

    private fun initClickListeners() {
        clickNextPage()
        clickPreviousPage()
        checkAllProduct()
        clickBackButton()
        clickOrderButton()
    }

    override fun setShoppingCart(shoppingCart: List<CartProduct>) {
        shoppingCartAdapter.submitList(shoppingCart)
    }

    override fun deleteProduct(index: Int) {
        shoppingCartAdapter.notifyItemRemoved(index)
    }

    override fun setPage(pageNumber: Int) {
        binding.textShoppingCartPageNumber.text = pageNumber.toString()
    }

    override fun setPageButtonEnable(previous: Boolean, next: Boolean) {
        binding.buttonShoppingCartNextPage.isEnabled = next
        binding.buttonShoppingCartPreviousPage.isEnabled = previous
    }

    private fun clickNextPage() {
        binding.buttonShoppingCartNextPage.setOnClickListener {
            presenter.goNextPage()
        }
    }

    private fun clickPreviousPage() {
        binding.buttonShoppingCartPreviousPage.setOnClickListener {
            presenter.goPreviousPage()
        }
    }

    override fun goProductDetailActivity(cartProduct: CartProduct) {
        val intent = ProductDetailActivity.getIntent(this, cartProduct.product.id)
        startActivity(intent)
    }

    override fun updateAllCheck(isChecked: Boolean) {
        binding.checkShoppingCartAll.isChecked = isChecked
    }

    override fun updateOrder(orderCount: Int) {
        binding.textShoppingCartOrder.text = getString(R.string.orderCount, orderCount)
    }

    override fun updatePayment(payment: Int) {
        binding.textShoppingCartPayment.text = getString(R.string.detailPriceFormat, payment)
    }

    override fun showUnExpectedError() {
        Toast.makeText(this, getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show()
    }

    private fun checkAllProduct() {
        binding.checkShoppingCartAll.setOnClickListener {
            presenter.selectAll((it as CheckBox).isChecked)
        }
    }

    private fun clickBackButton() {
        binding.buttonShoppingCartBack.setOnClickListener { finish() }
    }

    override fun showChangeLack(lackAmount: Int) {
        OrderDialog.makeDialog(lackAmount).show(supportFragmentManager, OrderDialog.TAG)
    }

    override fun showOrderComplete(orderId: Long) {
        startActivity(OrderDetailActivity.getIntent(this, orderId))
        finish()
    }

    private fun clickOrderButton() {
        binding.textShoppingCartOrder.setOnClickListener { presenter.requestOrder() }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ShoppingCartActivity::class.java)
        }
    }
}
