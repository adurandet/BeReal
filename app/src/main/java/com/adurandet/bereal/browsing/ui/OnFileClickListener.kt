package com.adurandet.bereal.browsing.ui

interface OnFileClickListener {
    fun onFileClicked(id: String)
    fun onFileDeleteClicked(id: String, parentId: String)
}
