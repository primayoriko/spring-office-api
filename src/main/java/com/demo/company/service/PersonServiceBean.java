package com.demo.company.service;

import com.demo.company.entity.Employee;
import com.demo.company.entity.Person;
import com.demo.company.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.demo.config.data.Credential;

@Service
public class PersonServiceBean implements PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceBean.class);

    @Autowired
    private PersonRepository personRepository;

    @Override
    public void create(Person person) throws Exception {
        personRepository.save(person);
    }

    @Override
    public void update(String personCode, Person person) throws Exception{
        Person oldPerson = personRepository.findFirstByPersonCodeAndMarkForDeleteFalse(personCode);
        oldPerson.setPersonName(person.getPersonName());
        oldPerson.setAddresses(oldPerson.getAddresses());
        personRepository.save(oldPerson);
    }

    @Override
    public Page<Person> find(Pageable pageable) throws Exception{
        return personRepository.findByMarkForDeleteFalse(pageable);
    }

    @Override
    public Person findByPersonCode(String personCode) throws Exception{
        return personRepository.findFirstByPersonCodeAndMarkForDeleteFalse(personCode);
    }

    @Override
    public void deleteByPersonCode(String personCode) throws Exception{
        this.personRepository.deleteByPersonCode(personCode);
    }

//    @Override
//    public void update(Integer empNo, Employee employee) throws Exception {
//        Employee oldEmp = employeeRepository.findFirstByEmpNoAndMarkForDeleteFalse(empNo);
//        oldEmp.setEmpName(employee.getEmpName());
//        oldEmp.setJob(employee.getJob());
//        oldEmp.setMgr(employee.getMgr());
//        oldEmp.setHireDate(employee.getHireDate());
//        oldEmp.setSal(employee.getSal());
//        oldEmp.setComm(employee.getComm());
//        oldEmp.setDepartment(employee.getDepartment());
//        employeeRepository.save(oldEmp);
//    }
//
//    @Override
//    public Page<Employee> find(Pageable pageable) throws Exception {
//        return employeeRepository.findByMarkForDeleteFalse(pageable);
//    }
//
//    @Override
//    public Employee findByCode(Integer code) throws Exception {
//        return employeeRepository.findFirstByEmpNoAndMarkForDeleteFalse(code);
//    }
//
//    @Override
//    public void deleteByEmpNo(Integer code) throws Exception {
//        this.employeeRepository.deleteByEmpNo(code);
//    }
}
