package ru.psuti.apache1337.homeowners.data.requests

import ru.psuti.apache1337.homeowners.data.requests.db.entity.RequestEntity
import ru.psuti.apache1337.homeowners.data.requests.remote.dto.RequestDTO
import ru.psuti.apache1337.homeowners.data.requests.remote.dto.RequestEditDTO
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestModel
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestStatus
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


fun List<RequestEntity>.toModel() : List<RequestModel> {
    val newList = ArrayList<RequestModel>()
    for (req in this) {
        newList.add(req.toModel())
    }
    return newList
}

fun List<RequestDTO>.toEntity() : List<RequestEntity> {
    val newList = ArrayList<RequestEntity>()
    for (req in this) {
        newList.add(req.toEntity())
    }
    return newList
}

fun RequestEntity.toModel() : RequestModel {
    return RequestModel(
        id = id,
        theme = this.theme,
        request = this.text,
        date = LocalDateTime.parse(this.date),
        status = this.statusId.toRequestStatusModel(),
        fileName = file ?: ""
    )
}

fun String.toCalendar(): Calendar {  // MOVE TO LOCAL DATE TIME PLEASE
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)  // MOVE TO LOCAL DATE TIME PLEASE
    val date = formatter.parse(this)!!  // MOVE TO LOCAL DATE TIME PLEASE
    return Calendar.getInstance().apply { time=date }  // MOVE TO LOCAL DATE TIME PLEASE
}  // MOVE TO LOCAL DATE TIME PLEASE
  // MOVE TO LOCAL DATE TIME PLEASE
fun Calendar.toFormatString(): String {  // MOVE TO LOCAL DATE TIME PLEASE
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT).format(this.time)  // MOVE TO LOCAL DATE TIME PLEASE
}  // MOVE TO LOCAL DATE TIME PLEASE

fun RequestDTO.toEntity() : RequestEntity {
    return RequestEntity(
        id!!,
        title,
        date!!,
        address!!,
        comment,
        status,
        client,
        fileName,
        Date()
    )
}

fun Int.toRequestStatusModel() : RequestStatus {
    when (this) {
        1 -> return RequestStatus.NEW
        2 -> return RequestStatus.ACCEPTED
        3 -> return RequestStatus.COMPLETE
        4 -> return RequestStatus.CANCELED
    }
    throw IllegalArgumentException("unknown status")
}

fun RequestStatus.toInt() : Int {
    return when (this) {
        RequestStatus.NEW -> 1
        RequestStatus.ACCEPTED -> 2
        RequestStatus.COMPLETE -> 3
        RequestStatus.CANCELED -> 4
    }
}

fun RequestModel.toDTO(userId: Int): RequestDTO {
    return RequestDTO(
        null,
        null,
        theme,
        null,
        null,
        request,
        status.toInt(),
        userId,
        fileName
    )
}

fun RequestModel.toEditDTO(): RequestEditDTO {
    return RequestEditDTO(
        theme,
        request,
        fileName
    )
}