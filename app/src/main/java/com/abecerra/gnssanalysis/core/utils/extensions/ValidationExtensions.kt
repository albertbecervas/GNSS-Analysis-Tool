package com.abecerra.gnssanalysis.core.utils.extensions

fun isEmailValid(email: CharSequence): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun String.isPasswordValid(): Boolean {
    return this.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]{8,}$"))
}

fun String.isValidDni(): Boolean {
    if (!isFieldValid(this)) return false
    val lastLetter = last().toLowerCase()
    val dniNumber = substring(0 until lastIndex)
    val calculatedLetter = "TRWAGMYFPDXBNJZSQVHLCKE"[dniNumber.toInt() % 23].toLowerCase()
    return lastLetter == calculatedLetter
}

fun isFieldValid(field: String): Boolean {
    return field.isNotEmpty() && !field.isWhitespace()
}

fun String.isValidPassport(): Boolean {
    val (letters, noLetters) = partition { it.isLetter() }
    return letters.length == 3 && noLetters.length == 6
}