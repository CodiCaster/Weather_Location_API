package com.example.api_memo;

import com.example.api_memo.api.kakao.service.KakaoAPIService;
import com.example.api_memo.form.LocationForm;
import com.example.api_memo.service.Convert;
import com.example.api_memo.api.weather.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class MainController {

    private final KakaoAPIService kakaoAPIService;
    private final WeatherService weatherService;

    private double latitude;
    private double longitude;
    private Convert convert;
    private Integer xLat;
    private Integer yLon;
    private String[] weatherInfo;
    private String region;

    public MainController(KakaoAPIService kakaoAPIService, WeatherService weatherService) {
        this.kakaoAPIService = kakaoAPIService;
        this.weatherService = weatherService;
    }

    @GetMapping("/result")
    public String showResult(Model model) {
        model.addAttribute("latitude", latitude);
        model.addAttribute("longitude", longitude);
        model.addAttribute("xLat", xLat);
        model.addAttribute("yLon", yLon);
        model.addAttribute("weatherInfo", weatherInfo);
        model.addAttribute("region", region);
        return "result.html";
    }

    @PostMapping("/location")
    public String getLocation(LocationForm locationForm) throws IOException {
        if (locationForm.getLatitude().isEmpty()) {
            return "여기는 에러";
        }
        latitude = Double.parseDouble(locationForm.getLatitude());
        longitude = Double.parseDouble(locationForm.getLongitude());
        convert = new Convert(latitude, longitude);
        convert.transfer(convert, 0);
        xLat = (int) convert.getxLat();
        yLon = (int) convert.getyLon();
        weatherInfo = weatherService.getApiWeather(xLat, yLon);
        region = kakaoAPIService.loadLocation(longitude, latitude);
        return "redirect:/result";
    }


}
