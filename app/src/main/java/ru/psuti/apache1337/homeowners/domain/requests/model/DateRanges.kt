package ru.psuti.apache1337.homeowners.domain.requests.model

enum class DateRanges(var text: String) {

    ALL(Constants.all_val),
    LAST_YEAR(Constants.last_year_val),
    LAST_QUARTER(Constants.last_quarter_val),
    LAST_MONTH(Constants.last_month_val);

    companion object {

        @JvmStatic
        val VALUES: List<String> = values().map { it.text }
    }

    private class Constants() {

        companion object {
            @JvmStatic
            val all_val: String = "За все время"
            @JvmStatic
            val last_year_val: String = "За последний год"
            @JvmStatic
            val last_quarter_val: String = "За последний квартал"
            @JvmStatic
            val last_month_val: String = "За предыдущий месяц"
        }
    }
}

