package tn.Backend.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import tn.Backend.dto.EmailDetails;
import tn.Backend.dto.EmployeDto;
import tn.Backend.dto.Response;
import tn.Backend.dto.UserDto;
import tn.Backend.entites.Role;
import tn.Backend.entites.User;
import tn.Backend.repository.PosteRepository;
import tn.Backend.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService{
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PosteRepository posteRepository;

    @Override
    public Response addEmploye(EmployeDto employeDto) {
        // Vérification si le CIN est déjà utilisé
        boolean cinExists = repository.findAll()
                .stream()
                .anyMatch(user -> employeDto.getCin().equals(user.getCin()));

        if (cinExists) {
            return Response.builder()
                    .responseMessage("Le numéro CIN est déjà utilisé!")
                    .build();
        }

        // Vérification de l'email
        boolean userExists = repository.findAll()
                .stream()
                .anyMatch(user -> employeDto.getEmail().equalsIgnoreCase(user.getEmail()));

        if (userExists) {
            return Response.builder()
                    .responseMessage("L'email fourni est déjà utilisé!")
                    .build();
        }

        // Ajout de l'employé
        User user = EmployeDto.toEntity(employeDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.EMPLOYE);
        repository.save(user);

        sendAgentRegistrationEmail(employeDto, null);

        return Response.builder()
                .responseMessage("Employé ajouté avec succès")
                .email(user.getEmail())
                .build();
    }




    @Override
    public Optional<User> findUserByIdAndRole(Long id, Role role) {
        return repository.findByIdAndRole(id, role);
    }
    @Override
    public Response revokeAccount(Long cin, boolean activate) {
        // Recherchez l'utilisateur par CIN dans votre système
        Optional<User> userOptional = repository.findUserByCin(cin);

        if (userOptional.isEmpty()) {
            return Response.builder()
                    .responseMessage("Utilisateur non trouvé pour le CIN spécifié")
                    .build();
        }

        // Extraire l'utilisateur de l'Optional
        User user = userOptional.get();

        // Vérifier si l'utilisateur a le rôle d'employé
        if (user.getRole() != Role.EMPLOYE) {
            return Response.builder()
                    .responseMessage("Impossible de révoquer ou d'activer le compte. L'utilisateur n'est pas un employé.")
                    .build();
        }

        // Révoquez ou activez le compte de l'utilisateur en fonction du boolean "activate"
        user.setIsEnabled(activate);
        repository.save(user);

        // Envoyer un email pour informer l'utilisateur du changement de statut
        sendAccountStatusEmail(user, activate);

        if (activate) {
            return Response.builder()
                    .responseMessage("Le compte de l'employé avec le CIN " + cin + " a été activé avec succès")
                    .build();
        } else {
            return Response.builder()
                    .responseMessage("Le compte de l'employé avec le CIN " + cin + " a été révoqué avec succès")
                    .build();
        }
    }

    private void sendAccountStatusEmail(User user, boolean activate) {
        String subject = activate ? "Activation de votre compte" : "Révocation de votre compte";
        String statusMessage = activate ? "activé" : "révoqué";

        // Contenu de l'email avec un style moderne
        String content = "<html>"
                + "<head>"
                + "<style>"
                + "    body { font-family: 'Arial', sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }"
                + "    .container { max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }"
                + "    .header { background-color: #007bff; color: white; padding: 15px; border-radius: 8px 8px 0 0; text-align: center; }"
                + "    .footer { margin-top: 20px; font-size: 12px; color: #888; text-align: center; }"
                + "    h2 { margin: 0; font-size: 20px; }"
                + "    p { font-size: 14px; line-height: 1.5; }"
                + "    .status { font-size: 16px; font-weight: bold; color: #007bff; }"
                + "    .note { margin: 20px 0; font-size: 12px; color: #555; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>"
                + "<h2>Notification de Compte</h2>"
                + "</div>"
                + "<p>Bonjour <strong>" + HtmlUtils.htmlEscape(user.getFirstname()) + "</strong>,</p>"
                + "<p>Nous souhaitons vous informer que votre compte a été <span class='status'>" + statusMessage + "</span> avec succès.</p>"
                + "<p>Si vous avez des questions ou des préoccupations, n'hésitez pas à nous contacter.</p>"
                + "<p class='note'>Merci de votre confiance.</p>"
                + "<div class='footer'>"
                + "<p>Cordialement,<br>L'équipe de la plateforme</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        // Préparer les détails de l'email
        EmailDetails emailDetails = EmailDetails.builder()
                .to(user.getEmail())
                .subject(subject)
                .messageBody(content)
                .build();

        // Envoyer l'email
        emailService.sendMail(emailDetails);
    }


    private void sendAgentRegistrationEmail(EmployeDto employeDto, HttpServletRequest request) {
        // Sujet de l'email
        String subject = "Bienvenue sur notre plateforme";

        // Contenu de l'email en HTML
        StringBuilder content = new StringBuilder();
        content.append("<html>")
                .append("<head>")
                .append("<style>")
                .append("    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; }")
                .append("    .container { max-width: 600px; margin: auto; padding: 20px; }")
                .append("    .footer { margin-top: 20px; font-size: 12px; color: #888; text-align: center; }")
                .append("    h2 { font-size: 24px; }")
                .append("    p { font-size: 14px; line-height: 1.6; }")
                .append("    .bold { font-weight: bold; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class='container'>")
                .append("<h2>Bienvenue sur notre plateforme</h2>")
                .append("<p>Chère " + HtmlUtils.htmlEscape(employeDto.getFirstname()) + ",</p>")
                .append("<p>Nous sommes ravis de vous accueillir parmi nous !</p>")
                .append("<p>Un agent a créé votre compte avec succès. Voici vos informations de connexion :</p>")
                .append("<p>Email : <span class='bold'>" + HtmlUtils.htmlEscape(employeDto.getEmail()) + "</span></p>")
                .append("<p>Mot de passe temporaire : <span class='bold'>" + HtmlUtils.htmlEscape(employeDto.getPassword()) + "</span></p>")
                .append("<p>Veuillez noter qu'il s'agit de votre mot de passe temporaire. ")
                .append("Nous vous recommandons fortement de le modifier immédiatement après votre première connexion.</p>")
                .append("<p>N'oubliez pas de conserver cet e-mail pour vos archives.</p>")
                .append("<div class='footer'>")
                .append("<p>Cordialement,<br>L'équipe de la plateforme</p>")
                .append("</div>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        // Envoyer l'email à l'employé
        EmailDetails emailDetails = EmailDetails.builder()
                .to(employeDto.getEmail())
                .subject(subject)
                .messageBody(content.toString())
                .build();

        emailService.sendMail(emailDetails);
    }
    @Override
    public Response updateEmploye(Long cin, EmployeDto employeDto) {
        // Recherchez l'employé par CIN dans votre système
        Optional<User> userOptional = repository.findUserByCin(cin);

        if (userOptional.isEmpty()) {
            return Response.builder()
                    .responseMessage("Employé non trouvé pour le CIN spécifié")
                    .build();
        }

        // Extraire l'employé de l'Optional
        User employe = userOptional.get();

        // Vérifier si l'utilisateur a le rôle d'employé
        if (employe.getRole() != Role.EMPLOYE) {
            return Response.builder()
                    .responseMessage("Impossible de mettre à jour les informations. L'utilisateur n'est pas un employé.")
                    .build();
        }

        // Mettre à jour les informations de l'employé avec les nouvelles données
        employe.setFirstname(employeDto.getFirstname());
        employe.setLastname(employeDto.getLastname());
        employe.setEmail(employeDto.getEmail());
        employe.setPhone(employeDto.getPhone());

        repository.save(employe);

        return Response.builder()
                .responseMessage("Les informations de l'employé avec le CIN " + cin + " ont été mises à jour avec succès")
                .build();
    }
    @Override
    public List<UserDto> getAllEmployes() {
        List<User> allEmployes = repository.findAllByRole(Role.EMPLOYE);
        return allEmployes.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }
    public Response deleteEmploye(Long cin) {
        Optional<User> userOptional = repository.findUserByCin(cin);

        if (userOptional.isEmpty()) {
            return Response.builder()
                    .responseMessage("Employé non trouvé pour le CIN spécifié")
                    .build();
        }

        // Supprimer l'employé de la base de données
        repository.delete(userOptional.get());

        return Response.builder()
                .responseMessage("L'employé avec le CIN " + cin + " a été supprimé avec succès")
                .build();
    }
    @Override
    public Response deleteUser(Long id) {
        // Recherchez l'utilisateur par ID dans votre système
        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isEmpty()) {
            return Response.builder()
                    .responseMessage("Employé non trouvé pour l'ID spécifié")
                    .build();
        }

        // Supprimez l'utilisateur
        repository.deleteById(id);

        return Response.builder()
                .responseMessage("L'employé avec l'ID " + id + " a été supprimé avec succès")
                .build();
    }
}
