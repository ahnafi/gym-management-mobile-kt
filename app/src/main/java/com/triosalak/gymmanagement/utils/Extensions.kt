package com.triosalak.gymmanagement.utils

// Letakkan di file Extensions.kt atau sejenisnya

fun Int?.toRupiahInstant(): String {
    // Jika angkanya null, langsung kembalikan "-"
    if (this == null) return "-"

    // Ubah angka menjadi string, lalu ubah menjadi reversed char array
    // contoh: 60000 -> "00006"
    val reversedString = this.toString().reversed()

    // Kelompokkan setiap 3 karakter, lalu gabungkan dengan titik
    // contoh: "000", "06" -> "000.06"
    val formattedReversed = reversedString.chunked(3).joinToString(".")

    // Balikkan lagi hasilnya dan tambahkan prefix "RP "
    // contoh: "60.000" -> "RP 60.000"
    return "RP ${formattedReversed.reversed()}"
}
