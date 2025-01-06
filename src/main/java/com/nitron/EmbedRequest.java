package com.nitron;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmbedRequest {
private String model;
private boolean truncate;
private String input;
}
