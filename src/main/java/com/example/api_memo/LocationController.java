package com.example.api_memo;

import com.example.api_memo.entity.LocationForm;
import com.example.api_memo.location.service.Convert;
import com.example.api_memo.location.service.LocationService;
import com.example.api_memo.weather.service.WeatherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

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
    public String db() {
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
//        xLat = 63;
//        yLon = 90;
        xLat = (int) convert.getxLat();
        yLon = (int) convert.getyLon();
        weatherInfo = WeatherService.getApiWeather(xLat, yLon);
//        region = locationService.getRegion(xLat, yLon);
        //63, 90
//        (latitude=35.8678275, longitude=127.1365699)
        loadLocation();
        return "redirect:/result";
    }

    public void loadLocation() {

        String REST_KEY = "KakaoAK 66cde5e53ebf0b49f4cc2f96aa169b6b";
        String urlString = "https://dapi.kakao.com/v2/local/geo/coord2regioncode?x=" + longitude + "&y=" + latitude;

        BufferedReader br = null;
        JSONObject obj = new JSONObject();
        ObjectMapper mapper = new ObjectMapper();

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", REST_KEY);
            // 응답 코드 확인
            int responseCode = conn.getResponseCode();

            // 응답 내용 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            region = "";
            JSONObject json = new JSONObject(response.toString());
            JSONArray documents = json.getJSONArray("documents");

            for (int i = 0; i < documents.length(); i++) {
                JSONObject document = documents.getJSONObject(i);

                if (document.getString("region_type").equals("B")) {
                    String region1 = document.getString("region_1depth_name");
                    String region2 = document.getString("region_2depth_name");
                    String region3 = document.getString("region_3depth_name");

                    region = region1 + " " + region2 + " " + region3;
                }
            }
            // 연결 종료
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
