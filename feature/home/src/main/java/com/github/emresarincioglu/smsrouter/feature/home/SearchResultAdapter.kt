package com.github.emresarincioglu.smsrouter.feature.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.github.emresarincioglu.smsrouter.core.designsystem.R

internal class SearchResultAdapter(
    private val searchHistory: List<String>,
    private val searchResults: List<String>,
    private val onResultItemClicked: (View) -> Unit,
    private val onHistoryItemClicked: (View) -> Unit,
    private val onHistoryItemLongClicked: (View) -> Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val textView = LayoutInflater.from(parent.context).inflate(
            android.R.layout.simple_list_item_1, parent, false
        ) as TextView

        return when (viewType) {

                VIEW_TYPE_SEARCH_HISTORY -> HistoryViewHolder(
                    parent.context,
                    textView,
                    onHistoryItemClicked,
                    onHistoryItemLongClicked
                )

                VIEW_TYPE_SEARCH_RESULT -> ResultViewHolder(
                    parent.context,
                    textView,
                    onResultItemClicked
                )

                else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {

            VIEW_TYPE_SEARCH_HISTORY -> {
                val historyItem = holder as HistoryViewHolder
                historyItem.text = searchHistory[position]
            }

            VIEW_TYPE_SEARCH_RESULT -> {
                val resultItem = holder as ResultViewHolder
                val resultPosition = position - searchHistory.size
                resultItem.text = searchResults[resultPosition]
            }

            else -> throw IllegalStateException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < searchHistory.size) {
            VIEW_TYPE_SEARCH_HISTORY
        } else {
            VIEW_TYPE_SEARCH_RESULT
        }
    }

    override fun getItemCount() = searchHistory.size + searchResults.size

    class HistoryViewHolder private constructor(private val textView: TextView) :
        RecyclerView.ViewHolder(textView) {

        var text: String
            get() = textView.text.toString()
            set(newText) {
                textView.text = newText
            }

        constructor(
            context: Context,
            view: TextView,
            onClickListener: (View) -> Unit,
            onLongClickListener: (View) -> Boolean
        ) : this(view) {

            view.setOnClickListener(onClickListener)
            view.setOnLongClickListener(onLongClickListener)

            val startIcon = AppCompatResources.getDrawable(context, R.drawable.ic_history_24)
            view.setCompoundDrawablesRelative(startIcon, null, null, null)
        }
    }

    class ResultViewHolder private constructor(private val textView: TextView) :
        RecyclerView.ViewHolder(textView) {

        var text: String
            get() = textView.text.toString()
            set(newText) {
                textView.text = newText
            }

        constructor(
            context: Context,
            view: TextView,
            onClickListener: (View) -> Unit
        ) : this(view) {

            view.setOnClickListener(onClickListener)

            val startIcon = AppCompatResources.getDrawable(context, R.drawable.ic_search_24)
            view.setCompoundDrawablesRelative(startIcon, null, null, null)
        }
    }

    companion object {
        private const val VIEW_TYPE_SEARCH_HISTORY = 0
        private const val VIEW_TYPE_SEARCH_RESULT = 1
    }
}