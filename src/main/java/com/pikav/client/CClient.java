package com.pikav.client;

import com.pikav.NioClient;
import java.io.IOException;

public class CClient {

    public static void main(String[] args) throws IOException {
        new NioClient().start("CClient");
    }

}
