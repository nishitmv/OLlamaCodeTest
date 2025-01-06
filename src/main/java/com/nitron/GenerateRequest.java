package com.nitron;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateRequest {
    String model;
    String prompt;
    boolean stream ;

}
