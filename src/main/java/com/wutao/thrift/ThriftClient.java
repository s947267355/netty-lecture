package com.wutao.thrift;

import com.wutao.thrift.generated.Person;
import com.wutao.thrift.generated.PersonService;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * @author tao.wu
 * @date 2019-04-17
 */
public class ThriftClient {

    public static void main(String[] args) {
        TTransport tTransport = new TFramedTransport(new TSocket("localhost", 8899), 600);
        TProtocol tProtocol = new TCompactProtocol(tTransport);
        PersonService.Client client = new PersonService.Client(tProtocol);

        try {
            tTransport.open();
            Person person = client.getPersonByUsername("张三");
            System.out.println("person: " + person.getUsername() + person.getAge() + person.isMarried());
            System.out.println("-----------");
            Person person1 = new Person();
            person1.setUsername("李四");
            person1.setAge(16);
            person1.setMarried(false);

            client.savePerson(person1);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            tTransport.close();
        }
    }
}
