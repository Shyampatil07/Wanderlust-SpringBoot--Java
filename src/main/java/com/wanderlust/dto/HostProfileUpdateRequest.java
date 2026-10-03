package com.wanderlust.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostProfileUpdateRequest {

    private String name;

    private String phone;

    private String about;
}