package ru.gb.weather.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class Weather(
    val city: City = getDefaultCity(),
    val temperature: Int = 0,
    val feelsLike: Int = 0,
    val dateUnixTime: Long = System.currentTimeMillis() / 1000, // 1522702800
    val forecastParts: List<ForecastPart> = listOf(
        ForecastPart(ForecastName.Evening),
        ForecastPart(ForecastName.Night)
    )
): Parcelable {
    val creationDateMillis: Long = System.currentTimeMillis()
    val creationDate: Date = Date(creationDateMillis)
    val date: Date = Date(dateUnixTime * 1000)

    fun getTemperatureString(): String = WeatherUtils.getTemperatureString(temperature)
    fun getFeelsLikeString(): String = WeatherUtils.getTemperatureString(feelsLike)
}

fun getDefaultCity() = City("Москва", 55.755826, 37.617299900000035)

fun getWorldCities() = listOf(
        Weather(City("Лондон", 51.5085300, -0.1257400), 1, 2),
        Weather(City("Токио", 35.6895000, 139.6917100), 3, 4),
        Weather(City("Париж", 48.8534100, 2.3488000), 5, 6),
        Weather(City("Берлин", 52.52000659999999, 13.404953999999975), 7, 8),
        Weather(City("Рим", 41.9027835, 12.496365500000024), 9, 10),
        Weather(City("Минск", 53.90453979999999, 27.561524400000053), 11, 12),
        Weather(City("Стамбул", 41.0082376, 28.97835889999999), 13, 14),
        Weather(City("Вашингтон", 38.9071923, -77.03687070000001), 15, 16),
        Weather(City("Киев", 50.4501, 30.523400000000038), 17, 18),
        Weather(City("Пекин", 39.90419989999999, 116.40739630000007), 19, 20)
    )

fun getRussianCities() = listOf(
        Weather(City("Москва", 55.755826, 37.617299900000035), 1, 2),
        Weather(City("Санкт-Петербург", 59.9342802, 30.335098600000038), 3, 3),
        Weather(City("Новосибирск", 55.00835259999999, 82.93573270000002), 5, 6),
        Weather(City("Екатеринбург", 56.83892609999999, 60.60570250000001), 7, 8),
        Weather(City("Нижний Новгород", 56.2965039, 43.936059), 9, 10),
        Weather(City("Казань", 55.8304307, 49.06608060000008), 11, 12),
        Weather(City("Челябинск", 55.1644419, 61.4368432), 13, 14),
        Weather(City("Омск", 54.9884804, 73.32423610000001), 15, 16),
        Weather(City("Ростов-на-Дону", 47.2357137, 39.701505), 17, 18),
        Weather(City("Уфа", 54.7387621, 55.972055400000045), 19, 20)
    )

// curl --header "X-Yandex-API-Key: key" "https://api.weather.yandex.ru/v2/informers?lat=55.755826&lon=37.617299900000035"
/*
* {
	"now": 1644749580,
	"now_dt": "2022-02-13T10:53:00.930827Z",
	"info": {
		"url": "https://yandex.ru/pogoda/10735?lat=55.815792\u0026lon=37.380031",
		"lat": 55.815792,
		"lon": 37.380031
	},
	"fact": {
		"obs_time": 1644746400,
		"temp": 1,
		"feels_like": -5,
		"icon": "ovc",
		"condition": "overcast",
		"wind_speed": 5.9,
		"wind_dir": "w",
		"pressure_mm": 752,
		"pressure_pa": 1002,
		"humidity": 78,
		"daytime": "d",
		"polar": false,
		"season": "winter",
		"wind_gust": 10.5
	},
	"forecast": {
		"date": "2022-02-13",
		"date_ts": 1644699600,
		"week": 6,
		"sunrise": "08:00",
		"sunset": "17:28",
		"moon_code": 14,
		"moon_text": "moon-code-14",
		"parts": [
			{
				"part_name": "evening",
				"temp_min": -2,
				"temp_avg": -1,
				"temp_max": -1,
				"wind_speed": 4.1,
				"wind_gust": 7.9,
				"wind_dir": "sw",
				"pressure_mm": 752,
				"pressure_pa": 1002,
				"humidity": 78,
				"prec_mm": 0,
				"prec_prob": 0,
				"prec_period": 360,
				"icon": "ovc",
				"condition": "overcast",
				"feels_like": -6,
				"daytime": "n",
				"polar": false
			},
			{
				"part_name": "night",
				"temp_min": -3,
				"temp_avg": -3,
				"temp_max": -2,
				"wind_speed": 5.2,
				"wind_gust": 9.9,
				"wind_dir": "sw",
				"pressure_mm": 751,
				"pressure_pa": 1001,
				"humidity": 74,
				"prec_mm": 0,
				"prec_prob": 0,
				"prec_period": 360,
				"icon": "ovc",
				"condition": "overcast",
				"feels_like": -9,
				"daytime": "n",
				"polar": false
			}
		]
	}
}
* */

