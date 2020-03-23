package com.github.mhdwajeeh.dialogspinner

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.github.mhdwajeeh.dialogspinner.R
import kotlinx.android.synthetic.main.dialog_spinner.view.*

class DialogSpinner : LinearLayout, ItemSelectionListener {


    companion object {
        public const val SELECTION_MODE_SINGLE = 0
        public const val SELECTION_MODE_MULTIPLE = 1
    }

    var selectionMode: Int = SELECTION_MODE_SINGLE

    var dataSet: MutableList<String> = mutableListOf()
        set(value) {
            field = value
            selectionList = listOf()
        }

    var itemSelectionListener: ItemSelectionListener? = null
    var selectionList: List<Int> = listOf()
        set(value) {
            field = value
            updateTextView()
        }


    constructor(context: Context?) : super(context) {
        initView(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(attrs)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private fun initView(attrs: AttributeSet?) {
        orientation = HORIZONTAL
        isClickable = true
        isFocusable = true
        gravity = Gravity.CENTER_VERTICAL

        View.inflate(context, R.layout.dialog_spinner, this)

        // get selection mode from attributes if present
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DialogSpinner,
            0, 0
        ).apply {
            try {
                selectionMode =
                    getInteger(R.styleable.DialogSpinner_ds_selection_mode, SELECTION_MODE_SINGLE)
            } finally {
                recycle()
            }
        }

        setOnClickListener {
            openDialog();
        }
    }

    private fun openDialog() {
        val dialog = DialogSpinnerPopup(context, selectionMode, dataSet, selectionList, this)

        dialog.show()
    }

    override fun onItemSelection(items: List<Int>) {
        selectionList = items
        updateTextView()
        itemSelectionListener?.onItemSelection(items)
    }

    private fun updateTextView() {
        spinner_text.text = when (selectionList.size) {
            0 -> "Select an option"
            1 -> dataSet[selectionList[0]]
            else -> "${selectionList.size} options selected"
        }
    }


}