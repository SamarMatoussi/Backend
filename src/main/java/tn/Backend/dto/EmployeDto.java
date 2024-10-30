package tn.Backend.dto;

import lombok.Getter;
import lombok.Setter;
import tn.Backend.auth.RegisterRequest;
import tn.Backend.entites.Employe;

@Getter
@Setter
public class EmployeDto extends RegisterRequest {
   // private Long posteId;

    public static RegisterRequest fromEntity(Employe employe) {
        if (employe == null) {
            return null;
        }
        return EmployeDto.builder()
                .firstname(employe.getFirstname())
                .lastname(employe.getLastname())
                .cin(employe.getCin())
                .email(employe.getEmail())
                .password(employe.getPassword())
                .phone(employe.getPhone())
                .build();
    }

    public static Employe toEntity(EmployeDto dto) {
        if (dto == null) {
            return null;
        }
        Employe employe = new Employe();
        employe.setCin(dto.getCin());
        employe.setFirstname(dto.getFirstname());
        employe.setLastname(dto.getLastname());
        employe.setEmail(dto.getEmail());
        employe.setPassword(dto.getPassword());
        employe.setPhone(dto.getPhone());
        return employe;
    }


}
