package com.example.bdmi.data.utils

object SortOptions {
    val values = mapOf(
        "popularity.desc" to "Popularity (High → Low)",
        "popularity.asc" to "Popularity (Low → High)",
        "revenue.desc" to "Revenue (High → Low)",
        "revenue.asc" to "Revenue (Low → High)",
        "primary_release_date.desc" to "Release Date (Newest)",
        "primary_release_date.asc" to "Release Date (Oldest)",
        "title.desc" to "Title (Z → A)",
        "title.asc" to "Title (A → Z)",
        "vote_average.desc" to "Rating (High → Low)",
        "vote_average.asc" to "Rating (Low → High)",
        "vote_count.desc" to "Vote Count (High → Low)",
        "vote_count.asc" to "Vote Count (Low → High)"
    )
}
