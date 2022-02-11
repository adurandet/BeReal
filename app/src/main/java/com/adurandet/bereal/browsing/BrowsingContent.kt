package com.adurandet.bereal.browsing

// Using a different model for each layer is a better practice
data class BrowsingContent(
    val id: String,
    val parentId: String,
    val name: String,
    val isDir: Boolean,
    val size: Int,
    val contentType: String,
)