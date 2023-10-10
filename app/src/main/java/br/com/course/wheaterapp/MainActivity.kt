    package br.com.course.wheaterapp

    import android.os.Bundle
    import android.util.Log
    import androidx.appcompat.app.AppCompatActivity
    import androidx.appcompat.widget.SearchView
    import br.com.course.wheaterapp.databinding.ActivityMainBinding
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import java.text.SimpleDateFormat
    import java.util.Date
    import java.util.Locale
    import java.util.TimeZone

    class MainActivity : AppCompatActivity() {
        private val binding : ActivityMainBinding by lazy{
            ActivityMainBinding.inflate(layoutInflater)
        }
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(binding.root)
            fetchWeatherData("Sao paulo")
            SearchCity()
        }

        private fun SearchCity() {
            val searchView = binding.searchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        fetchWeatherData(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

            })
        }

        private fun fetchWeatherData(cityName:String) {
            val retrofit  = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build().create(ApiInterface::class.java)
            val response = retrofit.getWeatherData(cityName, "701cacd84bf215e3802a69dc251adaf4", "metric")
            response.enqueue(object : Callback<WeatherApp>{
                override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                    val responseBody = response.body()
                    if(response.isSuccessful && responseBody != null){
                        val temperature = responseBody.main.temp.toString()
                        val humidity = responseBody.main.humidity
                        val windSpeed = responseBody.wind.speed
                        val sunRise = responseBody.sys.sunrise.toLong()
                        val sunSet = responseBody.sys.sunset.toLong()
                        val seaLevel = responseBody.main.pressure
                        val condition = responseBody.weather.firstOrNull()?.main?:"unknow"
                        val maxTemp = responseBody.main.temp_max
                        val minTemp = responseBody.main.temp_min

                        binding.temp.text = "$temperature °C"
                        binding.weather.text = condition
                        binding.maxTemp.text = "Max temp: $maxTemp °C"
                        binding.minTemp.text = "Min temp: $minTemp °C"
                        binding.humidity.text = "$humidity %"
                        binding.windSpeed.text = "$windSpeed m/s"
                        binding.sunrise.text = "${time(sunRise)}"
                        binding.sunset.text = "${time(sunSet)}"
                        binding.sea.text = "$seaLevel hPa"
                        binding.condition.text = condition
                        binding.day.text = dayName(System.currentTimeMillis())
                            binding.date.text = date()
                            binding.cityName.text = "$cityName"
                        changeImagsAccordingToWeatherCondition(condition)
                    }
                }

                override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })

            }

        private fun changeImagsAccordingToWeatherCondition(conditions:String) {
            when(conditions){
                "Clear Sky", "Sunny", "Clear" ->{
                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sun)
                }
                "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" ->{
                    binding.root.setBackgroundResource(R.drawable.colud_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloud)
                }
                "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" ->{
                    binding.root.setBackgroundResource(R.drawable.rain_background)
                    binding.lottieAnimationView.setAnimation(R.raw.rain)
                }
                "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                    binding.root.setBackgroundResource(R.drawable.snow_background)
                    binding.lottieAnimationView.setAnimation(R.raw.snow)
                }
                else ->{
                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sun)
                }
            }
            binding.lottieAnimationView.playAnimation()
        }

        private fun date(): String {
            val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
            return sdf.format(Date())
        }

        private fun time(timestamp: Long): String {
            // Define o fuso horário para o Brasil
            val timeZone = TimeZone.getTimeZone("America/Sao_Paulo")

            // Cria um objeto SimpleDateFormat com o fuso horário configurado
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.timeZone = timeZone

            // Formata a data usando o fuso horário do Brasil
            return sdf.format(Date(timestamp * 1000))
        }

        fun dayName(timestamp:Long):String{
            val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
            return sdf.format(Date())
        }
    }