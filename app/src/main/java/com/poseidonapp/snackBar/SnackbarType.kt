package com.poseidonapp.snackBar

enum class SnackbarType(val minHeight: Int, val maxHeight: Int, val maxLines: Int) {

    /**
     * Snackbar with a single line
     */
    SINGLE_LINE(48, 48, 1),
    /**
     * Snackbar with two lines
     */
    MULTI_LINE(48, 100, 3)
}
