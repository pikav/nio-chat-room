package com.pikav.client;

import com.pikav.NioClient;
import java.io.IOException;

public class AClient {

    public static void main(String[] args) throws IOException {
        new NioClient().start("AClient");
    }

}
