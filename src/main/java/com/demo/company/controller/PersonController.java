package com.demo.company.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.demo.company.entity.Address;
import com.demo.company.entity.Person;
import com.demo.company.service.PersonService;
import com.demo.dto.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.base.BaseResponse;
import com.demo.base.ListBaseResponse;
import com.demo.base.Metadata;
import com.demo.base.SingleBaseResponse;

@RestController
@RequestMapping(value = PersonControllerPath.BASE_PATH)
public class PersonController {

    @Autowired
    private PersonService personService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse create(@RequestBody PersonDTO request) throws Exception {
        this.personService.create(toPerson(request));
        return new BaseResponse(null, null, true, "");
    }

    @RequestMapping(value = PersonControllerPath.UPDATE_BY_CODE, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse updateByPersonCode(@PathVariable String code, @RequestBody PersonDTO request) throws Exception {
        this.personService.update(code, toPerson(request));
        return new BaseResponse(null, null, true, "");
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListBaseResponse<PersonDTO> find(@RequestParam(defaultValue = "0") Integer page,
                                                            @RequestParam(defaultValue = "10") Integer size) throws Exception {
        Pageable pageable = PageRequest.of(page, size);
        Page<Person> people = this.personService.find(pageable);
        List<PersonDTO> personResponses = people.getContent().stream().map(this::toDTO).collect(Collectors.toList());
        return new ListBaseResponse<>(null, null, true, "", personResponses,
                new Metadata(page, size, people.getTotalElements()));
    }

    @RequestMapping(value = PersonControllerPath.FIND_BY_CODE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleBaseResponse<PersonDTO> findByPersonCode(@PathVariable String code) throws Exception {
        Person person  = this.personService.findByPersonCode(code);
        PersonDTO response = Optional.ofNullable(person).map(this::toDTO).orElse(null);
        return new SingleBaseResponse<>(null, null, true, "", response);
    }

    @RequestMapping(value = PersonControllerPath.DELETE_BY_CODE, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse deleteByPersonCode(@PathVariable String code) throws Exception {
        this.personService.deleteByPersonCode(code);
        return new BaseResponse(null, null, true, "");
    }

    private PersonDTO toDTO(Person person) {
        return Optional.ofNullable(person).map(p -> {
            PersonDTO dto = PersonDTO.builder().build();
            BeanUtils.copyProperties(p, dto);
            AddressDTO[] addresses = Arrays.stream(p.getAddresses()).map(o -> toDTO(o)).toArray(size -> new AddressDTO[size]);
            dto.setAddresses(addresses);
            return dto;
        }).orElse(null);
    }

    private AddressDTO toDTO(Address address) {
        return Optional.ofNullable(address).map(a -> {
            AddressDTO dto = AddressDTO.builder().build();
            BeanUtils.copyProperties(a, dto);
            return dto;
        }).orElse(null);
    }

    private Person toPerson(PersonDTO dto){
        return Optional.ofNullable(dto).map(d -> {
            Person person = Person.builder().build();
            BeanUtils.copyProperties(d, person);
            Address[] addresses = Arrays.stream(dto.getAddresses()).map(o -> toAddress(o)).toArray(size -> new Address[size]);
            person.setAddresses(addresses);
            return person;
        }).orElse(null);
    }

    private Address toAddress(AddressDTO dto){
        return Optional.ofNullable(dto).map(d -> {
            Address address = Address.builder().build();
            BeanUtils.copyProperties(d, address);
            return address;
        }).orElse(null);
    }
}
