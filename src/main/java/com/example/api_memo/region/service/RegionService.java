package com.example.api_memo.region.service;

import com.example.api_memo.member.entity.Member;
import com.example.api_memo.weather.service.WeatherAPIService;
import com.example.api_memo.region.dto.LocationDTO;
import com.example.api_memo.region.entity.Point;
import com.example.api_memo.region.entity.Region;
import com.example.api_memo.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;
    private final WeatherAPIService weatherAPIService;
    private final KakaoAPIService kakaoAPIService;

    public void save(LocationDTO locationDTO, Member member) {
        if (member.getId() == 1);

        double latitude = Double.parseDouble(locationDTO.getLatitude());
        double longitude = Double.parseDouble(locationDTO.getLongitude());
        Point point = transferLocationToPoint(0, latitude, longitude);

        /**
         * 날짜 정보 사용할 상황에 적당히 수정해야한다.
         */
        //날씨 정보는 다른 도메인에서 행해야 할것 같다.
        //String[] weatherInfo = weatherAPIService.getApiWeather(point);
        String address = kakaoAPIService.loadRegion(longitude, latitude);
        Region region = new Region(latitude, longitude, point, address);
        regionRepository.save(region);
        //동시에 Member 의 RegionID 에도 연결해줘
    }

    /**
     * 이미 지역 정보가 있으면 단순히 save가 아닌 update 해야 함
     */
    public void update(LocationDTO locationDTO) {
        Optional<Region> regionOptional = regionRepository.findById(1L);
        if (regionOptional == null) {
            return;
        }
        Region region = regionOptional.get();

        double latitude = Double.parseDouble(locationDTO.getLatitude());
        double longitude = Double.parseDouble(locationDTO.getLongitude());
        Point point = transferLocationToPoint(0, latitude, longitude);
        String address = kakaoAPIService.loadRegion(longitude, latitude);

        region.update(latitude, longitude, point, address);

        regionRepository.save(region);
    }

    //위도, 경도를 x, y 좌표로 변환
    public Point transferLocationToPoint(int mode, double lat, double lon) {
        double xLat = 0;
        double yLon = 0;


        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //


        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        if (mode == 0) {
//            rs.lat = lat_X; //gps 좌표 위도
//            rs.lng = lng_Y; //gps 좌표 경도
            double ra = Math.tan(Math.PI * 0.25 + (lat) * DEGRAD * 0.5);
            ra = re * sf / Math.pow(ra, sn);
            double theta = lon * DEGRAD - olon;
            if (theta > Math.PI) theta -= 2.0 * Math.PI;
            if (theta < -Math.PI) theta += 2.0 * Math.PI;
            theta *= sn;
            double x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            double y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
            xLat = x;
            yLon = y;
//            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
//            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
        } else {
//            rs.x = lat_X; //기존의 x좌표
//            rs.y = lng_Y; //기존의 경도
            double xlat = xLat;
            double ylon = yLon;
            double xn = xlat - XO;
            double yn = ro - ylon + YO;
            double ra = Math.sqrt(xn * xn + yn * yn);
            if (sn < 0.0) {
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5;

            double theta;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                } else theta = Math.atan2(xn, yn);
            }
            double alon = theta / sn + olon;
//            rs.lat = alat * RADDEG; //gps 좌표 위도
//            rs.lng = alon * RADDEG; //gps 좌표 경도
            lat = alat * RADDEG;
            lon = alon * RADDEG;
        }
        return new Point(xLat, yLon);
    }
}
