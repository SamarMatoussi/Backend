package tn.Backend.auth;

import java.io.IOException;

import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import tn.Backend.config.JwtService;
import tn.Backend.entites.*;
import tn.Backend.repository.UserRepository;
import tn.Backend.token.Token;
import tn.Backend.token.TokenRepository;
import tn.Backend.token.TokenType;

import static tn.Backend.services.UserService.applicationUrl;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final ApplicationEventPublisher publisher;
/*
  public Response register(RegisterRequest userRequest, final HttpServletRequest request) {

    boolean userExists = repository.findAll()
            .stream()
            .anyMatch(user -> userRequest.getEmail().equalsIgnoreCase(user.getEmail()));

    if (userExists) {
      return  (Response.builder()
              .responseMessage("User with provided email  already exists!")
              .build());
    }

    User user ;
    User savedUser = null ;

      user =new Employé();
      user = EmployéDto.toEntity((EmployéDto)userRequest);
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      user.setRole(Role.CLIENT);
      var saveUsers = repository.save(user);

      publisher.publishEvent(new RegistrationCompleteEvent((Employé) saveUsers, applicationUrl(request)));

      return Response.builder()
              .responseMessage("register")
              .email(user.getEmail())
              .build();
    }
*/
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            )
    );
    var user = repository.findByEmail(request.getEmail())
            .orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .role(user.getRole().name())
            .build();
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
@PostConstruct
  public void createdefeultadm() {
    Administrateur user =new Administrateur();
    User savedUser = null;
    String email = "responsablesysteme@gmail.com";
    if (!repository.existsByEmail(email)) {
      user.setEmail("responsablesysteme@gmail.com");
      user.setPassword(new BCryptPasswordEncoder().encode("Administrateur123456*"));
      user.setFirstname("Admin");
      user.setLastname("Admin");
      user.setPhone("98745639");
      user.setIsEnabled(true);
      user.setRole(Role.ADMIN);
      savedUser = repository.save((Administrateur) user);
    }

  }


}
