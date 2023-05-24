package woowacourse.shopping.presentation.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.cart.CartService
import woowacourse.shopping.data.common.PreferenceUtil
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.presentation.model.CartProductInfoModel

class CartActivity : AppCompatActivity(), CartContract.View {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val presenter: CartContract.Presenter by lazy {
        CartPresenter(
            this,
            CartRepositoryImpl(CartService(PreferenceUtil(this))),
        )
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == SHOW_SKELETON_MESSAGE_CODE) {
                setLoadingUiVisible(false)
                Log.d("wooseok", "wooseok")
            }
        }
    }

    private fun setLoadingUiVisible(enable: Boolean) {
        if (enable) {
            binding.containerCart.visibility = View.GONE
            binding.flCartList.visibility = View.VISIBLE
            return
        }
        binding.containerCart.visibility = View.VISIBLE
        binding.flCartList.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        setLoadingUiVisible(true)
        initView()
        managePaging()
    }

    private fun setUpBinding() {
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
    }

    private fun initView() {
        initCartAdapter()
        setToolBar()
        Thread {
            runOnUiThread {
                updateView()
            }
        }.start()
        binding.presenter = presenter
    }

    private fun initCartAdapter() {
        cartAdapter = CartAdapter(
            presenter = presenter,
        )
        binding.recyclerCart.adapter = cartAdapter
    }

    private fun setToolBar() {
        setSupportActionBar(binding.toolbarCart.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back_24)
    }

    private fun updateView() {
        presenter.loadCurrentPageProducts()
        presenter.updateCurrentPageCartView()
        presenter.checkPlusPageAble()
        presenter.checkMinusPageAble()
    }

    private fun managePaging() {
        onPlusPage()
        onMinusPage()
    }

    private fun onPlusPage() {
        binding.buttonPlusPage.setOnClickListener {
            presenter.plusPage()
            updateView()
        }
    }

    private fun onMinusPage() {
        binding.buttonMinusPage.setOnClickListener {
            presenter.minusPage()
            updateView()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun setCartItems(productModels: List<CartProductInfoModel>) {
        cartAdapter.setItems(productModels)
        handler.sendMessage(
            Message().apply {
                what = SHOW_SKELETON_MESSAGE_CODE
            }
        )
    }

    override fun setUpPlusPageState(isEnable: Boolean) {
        binding.buttonPlusPage.isClickable = isEnable
        if (isEnable) {
            binding.buttonPlusPage.setImageResource(R.drawable.icon_right_page_true)
        } else {
            binding.buttonPlusPage.setImageResource(R.drawable.icon_right_page_false)
        }
    }

    override fun setUpMinusPageState(isEnable: Boolean) {
        binding.buttonMinusPage.isClickable = isEnable
        if (isEnable) {
            binding.buttonMinusPage.setImageResource(R.drawable.icon_left_page_true)
        } else {
            binding.buttonMinusPage.setImageResource(R.drawable.icon_left_page_false)
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }

        private const val SHOW_SKELETON_MESSAGE_CODE = 0
    }
}
