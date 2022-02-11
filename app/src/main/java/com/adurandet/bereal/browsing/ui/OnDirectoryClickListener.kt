package com.adurandet.bereal.browsing.ui

interface OnDirectoryClickListener {
    fun onDirectoryClicked(id: String, parentId: String)
    fun onDirectoryDeleteClicked(id: String, parentId: String)
}
