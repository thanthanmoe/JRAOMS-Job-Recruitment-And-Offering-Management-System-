package com.blackjack.jraoms.service;

import com.blackjack.jraoms.dto.AddCompanyDto;
import com.blackjack.jraoms.entity.Company;
import com.blackjack.jraoms.entity.Department;
import com.blackjack.jraoms.repository.CompanyRepository;
import com.blackjack.jraoms.repository.DepartmentRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final DepartmentRepository departmentRepository;

    public void addCompany(AddCompanyDto addCompanyDto){
        //Save company data
        var company = Company.builder()
                .name(addCompanyDto.getName())
                .email(addCompanyDto.getEmail())
                .phone(addCompanyDto.getPhone())
                .location(addCompanyDto.getLocation())
                .about(addCompanyDto.getAbout())
                .link(addCompanyDto.getLink())
                .build();
        companyRepository.save(company);

        //Save department data
        String[] departmentList = addCompanyDto.getDepartment().split("•");

        for (int i = 0; i < departmentList.length; i++) {
            String departmentName = departmentList[i].trim();
            if (!departmentName.isEmpty()) {
                boolean isDuplicate = false;
                for (int j = 0; j < i; j++) {
                    if (departmentList[j].trim().equalsIgnoreCase(departmentName)) {
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    Department department = Department.builder()
                            .name(departmentName)
                            .company(company)
                            .build();
                    departmentRepository.save(department);
                }
            }
        }
    }
    @Transactional
    public DataTablesOutput<Company> getCompany(DataTablesInput input) {
        return companyRepository.findAll(input);
    }
    public List<Company> findAll() {
    	return companyRepository.findAll();
    }
    public Optional<Company> findById(Integer id) {
    	return companyRepository.findById(id);
    }
    
    public Optional<Company> findByName(String name) {
    	return companyRepository.findByName(name);
    }

    public boolean updateCompany(Company inputEntity,String newDepartmentList) {
        boolean error = false;
 
        if (error == false) {
            Company dataEntity = companyRepository.findById(inputEntity.getId()).get();
            dataEntity.setName(inputEntity.getName());
            dataEntity.setEmail(inputEntity.getEmail());
            dataEntity.setPhone(inputEntity.getPhone());
            dataEntity.setLink(inputEntity.getLink());
            dataEntity.setLocation(inputEntity.getLocation());
            dataEntity.setAbout(inputEntity.getAbout());
            
            companyRepository.save(dataEntity);
            List<Department> departmentEntityList = departmentRepository.findByCompanyId(inputEntity.getId());
            Iterator<Department> it = departmentEntityList.iterator();
            
            String[] departmentList = newDepartmentList.split("•");
            for (int i = 0; i < departmentList.length; i++) {
                String departmentName = departmentList[i].trim();
                if (!departmentName.isEmpty()) {
                    boolean isDuplicate = false;
                    for (int j = 0; j < i; j++) {
                        if (departmentList[j].trim().equalsIgnoreCase(departmentName)) {
                            isDuplicate = true;
                            break;
                        }
                    }
                    if (!isDuplicate) {
                        Department department = Department.builder().name(departmentName).company(dataEntity).build();
                        Optional<Department> duplicateEntity = departmentRepository.findByCompanyIdAndNameAndEnable(dataEntity.getId(), departmentName,true);
                        if (duplicateEntity.isEmpty()) {
                        	department.setEnable(true);
                            departmentRepository.save(department);
                        } else {
                            error = true;
                        }

                    }
                }
            }
        }
        return error;
    }

}
