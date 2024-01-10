package com.kosign.dev.payload.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponseErrorTemplate(String message, String code, @JsonProperty("data") Object object){

}
