package com.food.ordering.zinger.ui.signup

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.ordering.zinger.R
import com.food.ordering.zinger.data.model.PlaceModel
import com.food.ordering.zinger.databinding.BottomSheetCampusListBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PlacePickerDialog : BottomSheetDialogFragment() {

    private val viewModel: SignUpViewModel by sharedViewModel()
    var bottomSheetDialog: BottomSheetDialog? = null
    private var placeClickListener: PlaceClickListener? = null
    var placesList:List<PlaceModel> = ArrayList()
        set(value) {
            field = value
        }
    lateinit var binding: BottomSheetCampusListBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        bottomSheetDialog = dialog as BottomSheetDialog
        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.bottom_sheet_campus_list,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        Handler().postDelayed({
            getPeekHeight()
        },500)
    }

    private fun initView(){
        val productAdapter = PlacesAdapter(context!!, placesList, object : PlacesAdapter.OnItemClickListener {
            override fun onItemClick(item: PlaceModel, position: Int) {
                placeClickListener?.onPlaceClick(item)
                Handler().postDelayed({
                    dialog?.dismiss()
                }, 250)
            }
        })
        binding.editextSearch.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchPlace(s.toString())
                productAdapter.notifyDataSetChanged()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
        binding.recyclerCampus.layoutManager = LinearLayoutManager(context!!)
        binding.recyclerCampus.adapter = productAdapter
        binding.imageClose.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PlaceClickListener) {
            placeClickListener = context
        } else {
            throw RuntimeException("$context must implement ItemClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        placeClickListener = null
    }

    fun setListener(placeClickListener: PlaceClickListener) {
        this.placeClickListener = placeClickListener
    }

    interface PlaceClickListener {
        fun onPlaceClick(place: PlaceModel)
    }

    var peekHeight = 0
    private lateinit var peekHeightListener: ViewTreeObserver.OnGlobalLayoutListener
    private fun getPeekHeight(){
        peekHeightListener = ViewTreeObserver.OnGlobalLayoutListener {
            peekHeight = binding.layoutPlaceList.height
            println("Peek height "+peekHeight)
            //bottomSheetDialog?.behavior?.peekHeight = peekHeight
            bottomSheetDialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            binding.layoutPlaceList.viewTreeObserver.removeOnGlobalLayoutListener(peekHeightListener)
        }
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(peekHeightListener)
    }
}
