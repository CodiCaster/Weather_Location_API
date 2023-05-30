package com.example.api_memo;

import com.example.api_memo.entity.LocationForm;
import com.example.api_memo.location.service.Convert;
import com.example.api_memo.location.service.LocationService;
import com.example.api_memo.weather.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class LocationController {

    private final LocationService locationService;
    private double latitude;
    private double longitude;
    private Convert convert;
    private Integer xLat;
    private Integer yLon;
    private String[] weatherInfo;
    private String region;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/db")
    public String db(){
        return "db.html";
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
        weatherInfo = WeatherService.getApiWeather(xLat, yLon);
        region = locationService.getRegion(xLat, yLon);
        //63, 90
        //(latitude=35.8678275, longitude=127.1365699)
        return "redirect:/result";
    }
}
