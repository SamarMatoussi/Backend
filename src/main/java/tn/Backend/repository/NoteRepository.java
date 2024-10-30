package tn.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.Backend.entites.Note;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    // Recherche des notes en fonction du CIN de l'employé

    @Query("SELECT n FROM Note n WHERE n.employe.cin = :cin")
    List<Note> findByEmployeCin(Long cin);

    Optional<Note> findByEmployeIdAndAgentIdAndKpiId(Long employeId , Long agentId , Long kpiId );
    Optional<Note>  findByEmployeIdAndKpiId(Long employeId   , Long kpiId );
}