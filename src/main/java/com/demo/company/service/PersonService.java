package com.demo.company.service;

import com.demo.company.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PersonService {
    public void create(Person person) throws Exception;

    public void update(String personCode, Person person) throws Exception;

    public Page<Person> find(Pageable pageable) throws Exception;

    public Person findByPersonCode(String personCode) throws Exception;

    public void deleteByPersonCode(String personCode) throws Exception;

}
