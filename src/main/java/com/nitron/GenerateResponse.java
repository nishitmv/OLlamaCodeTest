package com.nitron;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateResponse {
private String model;
private String response;
private String created_at;
private boolean done;
private String done_reason;
}
