package com.example.scoutinginputapp

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

data class Team(
    val id: String = java.util.UUID.randomUUID().toString(),
    val number: String,
    val name: String,
    val score: Int = 0,
    val notes: String = ""
) {
    fun toQrString(): String {
        return "Team: $number, Name: $name, Score: $score, Notes: $notes"
    }
}

fun generateQrCode(content: String): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
    }
    return bitmap
}
