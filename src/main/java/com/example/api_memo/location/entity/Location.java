package com.example.api_memo.location.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor
public class Location {

    @Id
    @Column(name = "region_id")
    private Long id; // 지역 순번

    @Column(name = "region_parent")
    private String parentRegion; // 시, 도

    @Column(name = "region_child")
    private String childRegion; // 시, 군, 구

    @Column
    private Integer nx; // x좌표
    @Column
    private Integer ny; // y좌표

    private String temperatureMinimum;
    private String temperatureMaximum;

//    @Embedded
//    private Weather weather; // 지역 날씨 정보

    // 날씨 정보 제외하고 지역 생성
    public Location(Long id, String parentRegion, String childRegion, Integer nx, Integer ny) {
        this.id = id;
        this.parentRegion = parentRegion;
        this.childRegion = childRegion;
        this.nx = nx;
        this.ny = ny;
    }

    //날씨 갱신
    public void updateRegionWeather(String[] weatherInfos) {
        this.temperatureMinimum = weatherInfos[0];
        this.temperatureMaximum = weatherInfos[1];
    }


    @Override
    public String toString() {
        return parentRegion + " " + childRegion;
    }
}
