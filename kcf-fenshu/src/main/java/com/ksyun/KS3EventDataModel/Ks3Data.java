package com.ksyun.KS3EventDataModel;

import lombok.Data;

@Data
public class Ks3Data {
    Bucket bucket; // Bucket object of KS3. 
    DataObject object;
}
