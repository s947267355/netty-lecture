package com.wutao.thrift;

import com.wutao.thrift.generated.DataException;
import com.wutao.thrift.generated.Person;
import com.wutao.thrift.generated.PersonService;
import org.apache.thrift.TException;

/**
 * @author tao.wu
 * @date 2019-04-17
 */
public class PersonServiceImpl implements PersonService.Iface {

    @Override
    public Person getPersonByUsername(String username) throws DataException, TException {
        System.out.println("getPersonByUsername");
        Person person = new Person();
        person.setUsername("张三");
        person.setAge(21);
        person.setMarried(true);
        return person;
    }

    @Override
    public void savePerson(Person person) throws DataException, TException {
        System.out.println("save person:" + person.getUsername() + " " + person.getAge() + " "+ person.isMarried());
    }
}
