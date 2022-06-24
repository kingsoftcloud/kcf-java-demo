package com.ksyun.KS3EventDataModel;

import lombok.Data;

@Data
public class Bucket {
    String name; // KS3 Bucket name.
    String ownerid; // UID of KSCloud, represent KS3 owner account. 
}