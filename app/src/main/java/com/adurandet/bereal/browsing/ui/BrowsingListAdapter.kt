package com.adurandet.bereal.browsing.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adurandet.bereal.browsing.BrowsingContent
import com.adurandet.bereal.databinding.BrowsingDirectoryItemBinding
import com.adurandet.bereal.databinding.BrowsingFileItemBinding

class ContentListAdapter(
    private val onDirectoryClickListener: OnDirectoryClickListener,
    private val onFileClickListener: OnFileClickListener
) : ListAdapter<BrowsingContent, ContentViewHolder>(ContentDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return if(getItem(position).isDir) {
            BrowsingViewType.DIRECTORY.ordinal
        } else {
            BrowsingViewType.FILE.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BrowsingViewType.values()[viewType].createViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        // Using a ui model for each type of row would be a better practice
        when (holder) {
            is DirectoryViewHolder -> {
                holder.bind(getItem(position) as BrowsingContent, onDirectoryClickListener)
            }
            is FileViewHolder -> {
                holder.bind(getItem(position) as BrowsingContent, onFileClickListener)
            }
        }
    }
}

abstract class ContentViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView)

class DirectoryViewHolder(private val binding: BrowsingDirectoryItemBinding) : ContentViewHolder(binding.root) {
    fun bind(data: BrowsingContent, onDirectoryClickListener: OnDirectoryClickListener) {
        with(binding) {
            browsingName.text = data.name
            root.setOnClickListener {
                onDirectoryClickListener.onDirectoryClicked(data.id, data.parentId)
            }
            deleteIcon.setOnClickListener {
                onDirectoryClickListener.onDirectoryDeleteClicked(data.id, data.parentId)
            }
        }
    }
}

class FileViewHolder(private val binding: BrowsingFileItemBinding) : ContentViewHolder(binding.root) {
    fun bind(data: BrowsingContent, onFileClickListener: OnFileClickListener) {
        with(binding) {
            browsingName.text = data.name
            root.setOnClickListener {
                onFileClickListener.onFileClicked(data.id)
            }
            deleteIcon.setOnClickListener {
                onFileClickListener.onFileDeleteClicked(data.id, data.parentId)
            }
        }
    }
}

private class ContentDiffCallback : DiffUtil.ItemCallback<BrowsingContent>() {
    override fun areItemsTheSame(oldItem: BrowsingContent, newItem: BrowsingContent) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: BrowsingContent, newItem: BrowsingContent) =
        oldItem.id == newItem.id
}

enum class BrowsingViewType {
    DIRECTORY{
        override fun createViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup,
            attachToRoot: Boolean
        ): DirectoryViewHolder {
            val binding = BrowsingDirectoryItemBinding.inflate(
                inflater,
                parent,
                attachToRoot
            )
            return DirectoryViewHolder(binding)
        }
    },
    FILE {
        override fun createViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup,
            attachToRoot: Boolean
        ): FileViewHolder {
            val binding = BrowsingFileItemBinding.inflate(
                inflater,
                parent,
                attachToRoot
            )
            return FileViewHolder(binding)
        }
    };

    abstract fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        attachToRoot: Boolean = false
    ): ContentViewHolder
}