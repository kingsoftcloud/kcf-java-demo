package com.ksyun.KS3EventDataModel;

import lombok.Data;

@Data
public class Ks3CloudEventData {
    RequestData request;

    ResponseData response;

    Ks3Data ks3;

}
