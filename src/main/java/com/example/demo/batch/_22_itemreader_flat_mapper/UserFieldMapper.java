package com.example.demo.batch._22_itemreader_flat_mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

//将解析出来的数据进行封装
public class UserFieldMapper implements FieldSetMapper<User> {
    @Override
    public User mapFieldSet(FieldSet fieldSet) throws BindException {

        User user = new User();
        user.setId(fieldSet.readLong("id"));
        user.setName(fieldSet.readString("name"));
        user.setAge(fieldSet.readInt("age"));

        String address = "" + fieldSet.readString("province")
                + fieldSet.readString("city")
                + fieldSet.readString("area");
        user.setAddress(address);

        return null;
    }
}
