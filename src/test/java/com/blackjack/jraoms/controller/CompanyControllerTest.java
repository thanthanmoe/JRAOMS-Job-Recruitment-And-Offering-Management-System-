package com.blackjack.jraoms.controller;

import com.blackjack.jraoms.entity.Company;
import com.blackjack.jraoms.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyControllerTest {
    @Mock private CompanyRepository companyRepository;
    @InjectMocks private CompanyController companyController;
    @Test
    public void testCompanyList() {
        var companyOne = Company.builder()
                .name("one")
                .email("one@gmail.com")
                .phone("09123456")
                .location("one")
                .about("one")
                .link("www.one.com")
                .departments(null)
                .build();
        var companyTwo = Company.builder()
                .name("two")
                .email("two@gmail.com")
                .phone("09123456")
                .location("two")
                .about("two")
                .link("www.two.com")
                .departments(null)
                .build();
        List<Company> mockCompanies = Arrays.asList(companyOne, companyTwo);
        when(companyRepository.findAll()).thenReturn(mockCompanies);
        List<Company> result = companyController.companyList();
        verify(companyRepository, times(1)).findAll();
        assertEquals(mockCompanies, result);

    }

}