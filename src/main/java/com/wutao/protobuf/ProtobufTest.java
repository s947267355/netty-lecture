package com.wutao.protobuf;

import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author tao.wu
 * @date 2019-04-17
 */
public class ProtobufTest {

    public static void main(String[] args) throws InvalidProtocolBufferException {
        DataInfo.Student student = DataInfo.Student.newBuilder()
                .setName("张三")
                .setAge(23)
                .setAddress("宝安")
                .build();

        System.out.println(new Gson().toJson(student));

        byte[] byteArray = student.toByteArray();

        DataInfo.Student parseFrom = DataInfo.Student.parseFrom(byteArray);
        System.out.println(parseFrom.getName() + " " + parseFrom.getAge() + " " + parseFrom.getAddress());
    }
}
