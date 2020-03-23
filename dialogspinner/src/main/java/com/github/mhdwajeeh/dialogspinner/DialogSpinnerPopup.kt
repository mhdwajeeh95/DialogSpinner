package com.github.mhdwajeeh.dialogspinner

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mhdwajeeh.dialogspinner.R
import kotlinx.android.synthetic.main.dialog_spinner_popup.*

class DialogSpinnerPopup(
    context: Context,
    var selectionMode: Int,
    dataSet: MutableList<String>,
    selectionList: List<Int>,
    var itemSelectionListener: ItemSelectionListener?
) : Dialog(context), ItemSelectionListener {

    var recyclerAdapter: DialogSpinnerRecyclerAdapter? = null

    init {
        recyclerAdapter =
            DialogSpinnerRecyclerAdapter(selectionMode, dataSet, selectionList.toMutableSet(), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_spinner_popup)

        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.adapter = recyclerAdapter

        search_clear_btn.setOnClickListener { search_et.setText("") }

        ok_btn.visibility =
            if (selectionMode == DialogSpinner.SELECTION_MODE_SINGLE) View.GONE else View.VISIBLE
        ok_btn.setOnClickListener { dismiss() }

        search_et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                recyclerAdapter?.searchQuery = s?.toString() ?: ""
            }

        })
    }

    override fun onItemSelection(items: List<Int>) {
        if (selectionMode == DialogSpinner.SELECTION_MODE_SINGLE)
            this.dismiss()
        this.itemSelectionListener?.onItemSelection(items)
    }

    fun getSelection(): List<Int> {
        return recyclerAdapter?.getSelection() ?: listOf()
    }
}