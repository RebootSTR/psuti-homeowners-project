package ru.psuti.apache1337.homeowners.domain.requests.model

import ru.psuti.apache1337.homeowners.R

enum class RequestStatus(var text: String, var color: Int) {

    NEW(Constants.new_val, R.color.gray),
    ACCEPTED(Constants.accepted_val, R.color.black),
    CANCELED(Constants.canceled_val, R.color.red),
    COMPLETE(Constants.complete_val, R.color.green);

    companion object {

        @JvmStatic
        val VALUES: List<String> = values().map { it.text }
    }

    private class Constants() {

        companion object {
            @JvmStatic
            val new_val :String = "Новая"
            @JvmStatic
            val accepted_val :String = "Принята"
            @JvmStatic
            val canceled_val :String = "Отклонена"
            @JvmStatic
            val complete_val :String = "Выполнена"
        }
    }
}

