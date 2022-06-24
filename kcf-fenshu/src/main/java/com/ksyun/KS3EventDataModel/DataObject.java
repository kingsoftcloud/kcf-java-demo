package com.ksyun.KS3EventDataModel;

import lombok.Data;

@Data
public class DataObject {
    String internalurl; // Actual return Null. 
    String etag;
    String objectsize; // Size of file. 
    String url; // Actual return Null.
    String key; // filename with prefix. 
}
