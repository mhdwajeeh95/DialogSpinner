package com.github.mhdwajeeh.dialogspinner


import android.app.Dialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mhdwajeeh.dialogspinner.R
import kotlinx.android.synthetic.main.dialog_spinner_item.view.*
import java.util.*

class DialogSpinnerRecyclerAdapter(
    private var selectionMode: Int,
    private var dataSet: MutableList<String>,
    private var selectionList: MutableSet<Int>,
    var itemSelectionListener: ItemSelectionListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var searchDataSet: MutableList<Pair<String, Int>> = mutableListOf()

    var searchQuery: String = ""
        set(value) {
            field = value


            if (value.isEmpty()) {
                // no search query (search mode off)
                searchDataSet.clear()
            } else {
                // fill searchDataSet
                searchDataSet = dataSet
                    .mapIndexed { index, s -> Pair(s, index) }
                    .filter { pair -> pair.first.contains(value, ignoreCase = true) }
                    .toMutableList()
            }

            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewItem: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.dialog_spinner_item, parent, false)
        return ItemViewHolder(viewItem)
    }

    override fun getItemCount() = if (searchQuery.isEmpty()) dataSet.size else searchDataSet.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (searchQuery.isEmpty())
            (holder as ItemViewHolder).bind(dataSet[position], position)
        else (holder as ItemViewHolder).bindSearchMode(
            searchDataSet[position],
            position
        )
    }

    fun getSelection(): List<Int> {
        return selectionList.toList()
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(str: String, position: Int) {
            itemView.setOnClickListener {
                if (selectionMode == DialogSpinner.SELECTION_MODE_SINGLE && !selectionList.contains(
                        position
                    )
                ) {
                    val oldPos = if (selectionList.isNotEmpty()) selectionList.first() else -1
                    selectionList.clear()
                    selectionList.add(position)

                    if (oldPos != -1) notifyItemChanged(oldPos)
                    notifyItemChanged(position)

                } else if (selectionMode == DialogSpinner.SELECTION_MODE_MULTIPLE) {
                    if (selectionList.contains(position)) {
                        selectionList.remove(position)
                    } else {
                        selectionList.add(position)
                    }
                    notifyItemChanged(position)
                }

                itemSelectionListener?.onItemSelection(getSelection())
            }
            itemView.apply {
                // alternating item background
                background = if (position % 2 == 1)
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.spinner_item_clickable_bg2,
                        null
                    )
                else ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.spinner_item_clickable_bg1,
                    null
                )

                textview.text = HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY)
                check_img.visibility =
                    if (selectionList.isNotEmpty() && selectionList.contains(position))
                        View.VISIBLE
                    else View.GONE
            }
        }

        fun bindSearchMode(dataItem: Pair<String, Int>, position: Int) {
            itemView.setOnClickListener {
                if (selectionMode == DialogSpinner.SELECTION_MODE_SINGLE && !selectionList.contains(
                        dataItem.second
                    )
                ) {
                    val oldPos = if (selectionList.isNotEmpty()) selectionList.first() else -1
                    selectionList.clear()
                    selectionList.add(dataItem.second)

                    val posInSearch = searchDataSet.indexOfFirst { pair ->
                        pair.second == oldPos
                    }
                    if (posInSearch != -1) notifyItemChanged(posInSearch)

                    notifyItemChanged(position)

                } else if (selectionMode == DialogSpinner.SELECTION_MODE_MULTIPLE) {
                    if (selectionList.contains(dataItem.second)) {
                        selectionList.remove(dataItem.second)
                    } else {
                        selectionList.add(dataItem.second)
                    }
                    notifyItemChanged(position)
                }

                itemSelectionListener?.onItemSelection(getSelection())
            }
            itemView.apply {
                // alternating item background
                background = if (position % 2 == 1)
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.spinner_item_clickable_bg2,
                        null
                    )
                else ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.spinner_item_clickable_bg1,
                    null
                )

                textview.text =
                    HtmlCompat.fromHtml(dataItem.first, HtmlCompat.FROM_HTML_MODE_LEGACY)
                check_img.visibility =
                    if (selectionList.isNotEmpty() && selectionList.contains(dataItem.second))
                        View.VISIBLE
                    else View.GONE
            }
        }
    }

}