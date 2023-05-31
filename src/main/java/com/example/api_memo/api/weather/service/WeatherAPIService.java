package com.example.api_memo.api.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class WeatherAPIService {

    @Value("${api.weather.key}")
    private String REST_KEY;
    private String temperatureMinumum;
    private String temperatureMaximum;

    public String[] getApiWeather(int xLan, int yLon) throws IOException {
        String[] bases = calcBaseDateAndTime();
        String baseDate = bases[0];
        String baseTime = bases[1];

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + REST_KEY); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("200", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /*‘XX년 X월 XX일발표*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /*XX시 발표*/
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode("" + xLan, "UTF-8")); /*예보지점의 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode("" + yLon, "UTF-8")); /*예보지점의 Y 좌표값*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
//        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonData = objectMapper.readTree(sb.toString());

            JsonNode itemNode = jsonData
                    .path("response")
                    .path("body")
                    .path("items")
                    .path("item");

            if (itemNode.isArray()) {
                for (JsonNode node : itemNode) {
                    String category = node.path("category").asText();
                    if (category.equals("TMN")) {
                        temperatureMinumum = node.path("fcstValue").asText();
                    }
                    if (category.equals("TMX")) {
                        temperatureMaximum = node.path("fcstValue").asText();
                        break;
                    }
                }
            } else {
                System.out.println("No item found in JSON data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{temperatureMinumum, temperatureMaximum};
    }

    private String[] calcBaseDateAndTime() {
        LocalTime nowTime = LocalTime.now();
        LocalDate nowDate = LocalDate.now();

        LocalTime dateStandard = LocalTime.of(2, 10, 0);

        String baseTime = "0200";
        if (nowTime.isBefore(dateStandard)) {
            nowDate = nowDate.minusDays(1);
            baseTime = "2300";
        }
        String baseDate = nowDate.toString().replaceAll("-", "");
        return new String[]{baseDate, baseTime};
    }
}
