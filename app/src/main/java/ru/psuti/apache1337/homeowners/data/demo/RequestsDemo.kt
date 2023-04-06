package ru.psuti.apache1337.homeowners.data.demo

import ru.psuti.apache1337.homeowners.data.requests.db.entity.RequestEntity
import ru.psuti.apache1337.homeowners.data.requests.toInt
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestStatus
import java.text.SimpleDateFormat
import java.util.*

object RequestsDemo {

    private val zeroDate = Date().apply { time = 0 }
    private val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
    private val zeroDateText = formatter.format(zeroDate.time)
    private var id = 1;

    // да, с большой буквы, потому что не действие, а обертка
    private fun DemoRequest(
        theme: String,
        text: String,
        status: RequestStatus,
        filename: String? = null
    ) = RequestEntity(
        id++,
        theme,
        zeroDateText,
        0,
        text,
        status.toInt(),
        0,
        filename,
        zeroDate
    )

    val demoRequests = listOf(
        DemoRequest(
            "Нет горячей воды",
            "Ждем горячую воду уже 3 недели, когда ее уже дадут? Наш ребенок плачет, видимо, 98 градусов, для него мало....",
            RequestStatus.NEW
        ),
        DemoRequest(
            "Хочу есть",
            "Привезите пиццу, пожалуйста))",
            RequestStatus.CANCELED
        ),
        DemoRequest(
            "Купил гараж",
            "Купил гараж, прикладываю документ о купле продаже",
            RequestStatus.NEW,
            "123NSKJDN_PHOTO.jpg",
        ),
        DemoRequest(
            "Родился ребенок",
            "Родился яребенок, прикладываю свидетельство о рождении",
            RequestStatus.NEW,
            "A1938JDN_PHOTO.jpg",
        )
    )
}
