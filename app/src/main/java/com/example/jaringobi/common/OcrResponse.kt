package com.example.jaringobi.common

data class OcrResponse(
    val version: String,
    val requestId: String,
    val timestamp: Long,
    val images: List<Image>,
)

data class Image(
    val receipt: ReceiptInfo,
    val uid: String,
    val name: String,
    val inferResult: String,
    val message: String,
)

data class ReceiptInfo(
    val result: Result,
)

data class Result(
    val storeInfo: StoreInfo,
    val paymentInfo: PaymentInfo,
    val subResults: List<SubResult>,
    val totalPrice: TotalPrice,
)

data class StoreInfo(
    val name: TextField,
    val subName: TextField,
    val bizNum: TextField,
    val addresses: List<TextField>,
    val tel: List<TextField>,
)

data class TextField(
    val text: String,
    val formatted: FormattedText,
)

data class FormattedText(
    val value: String,
)

data class PaymentInfo(
    val date: DateInfo,
    val time: TimeInfo,
    val cardInfo: CardInfo,
    val confirmNum: ConfirmNum,
)

data class DateInfo(
    val text: String,
    val formatted: FormattedDate,
)

data class FormattedDate(
    val year: String,
    val month: String,
    val day: String,
)

data class TimeInfo(
    val text: String,
    val formatted: FormattedTime,
)

data class FormattedTime(
    val hour: String,
    val minute: String,
    val second: String,
)

data class CardInfo(
    val company: TextField,
    val number: TextField,
)

data class ConfirmNum(
    val text: String,
)

data class SubResult(
    val items: List<Item>,
)

data class Item(
    val name: TextField,
    val count: TextField,
    val price: Price,
)

data class Price(
    val price: TextField,
    val unitPrice: TextField,
)

data class TotalPrice(
    val price: TextField,
)

data class ValidationResult(
    val result: String,
)
