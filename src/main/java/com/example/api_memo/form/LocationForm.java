package com.example.api_memo.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationForm {
    private String latitude;
    private String longitude;
}