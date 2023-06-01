package com.example.api_memo.region.controller;

import com.example.api_memo.member.entity.Member;
import com.example.api_memo.region.dto.LocationDTO;
import com.example.api_memo.region.service.RegionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class RegionController {

    private final RegionService regionService;

//    @GetMapping("/result")
//    public String showResult(Model model) {
//        model.addAttribute("latitude", latitude);
//        model.addAttribute("longitude", longitude);
//        model.addAttribute("xLat", pointX);
//        model.addAttribute("yLon", pointY);
//        model.addAttribute("weatherInfo", weatherInfo);
//        model.addAttribute("address", address);
//        return "result.html";
//    }

    //위치 정보 갱신 버튼을 누르면 위치 갱신
    @PostMapping("/location")
    public String save(LocationDTO locationDTO, Member member) {
        if (locationDTO.getLatitude().isEmpty()) {
            return "여기는 에러";
        }
        regionService.save(locationDTO, member);

        return "result.html";
    }


}
