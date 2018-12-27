package com.testnetdeve.custom.client;

import com.testnetdeve.NettyConstant;

import java.util.ArrayList;
import java.util.List;

public class ClientsTest {
    public static void main(String[] args) {
        List<Client> list = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Client client = new Client();
            try {
                client.connect(NettyConstant.LOCAL_PORT + 1,NettyConstant.LOCALIP);
            } catch (Exception e) {
                e.printStackTrace();
            }
            list.add(client);
        }


    }
}
