package tn.Backend.dto;

import lombok.Getter;
import lombok.Setter;
import tn.Backend.auth.RegisterRequest;
import tn.Backend.entites.Administrateur;
import tn.Backend.entites.Agent;
import tn.Backend.entites.Role;

@Getter
@Setter
public class AgentDto extends RegisterRequest {

    public static Agent toEntity(AgentDto request) {
        return Agent.builder()
                .isEnabled(true)
                .cin(request.getCin())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .password(request.getPassword())
                .email(request.getEmail())
                .role(request.getRole())
                .phone(request.getPhone())
                .build();
    }
    public static AgentDto fromEntity(Agent request) {
        if (request == null) {
            return null;
        }

        return (AgentDto) AgentDto.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .cin(request.getCin())
                .email(request.getEmail())
                .role(request.getRole())
                .phone(request.getPhone())
                .build();
    }

}
