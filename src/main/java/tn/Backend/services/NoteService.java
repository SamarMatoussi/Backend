package tn.Backend.services;

import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import tn.Backend.dto.NoteDto;
import tn.Backend.exception.ResourceNotFound;

import java.io.IOException;
import java.util.List;

public interface NoteService {

    NoteDto ajouterNote(NoteDto noteDto , Authentication authentication) throws ResourceNotFound;

    List<NoteDto> obtenirNotesParEmployeCin(Long cin);

    @Transactional
    NoteDto mettreAJourNoteByCin(Long cin, NoteDto noteDto) throws ResourceNotFound;

    @Transactional
    void supprimerNoteByCin(Long cin) throws ResourceNotFound;

    void supprimerNote(Long id) throws ResourceNotFound;

    NoteDto mettreAJourNote(Long id, NoteDto noteDto) throws ResourceNotFound;
    List<NoteDto> getAllNotes();

    NoteDto getNote(Long employeId , Long kpiId , Authentication authentication) throws ResourceNotFound;
    NoteDto getNoteByEmployeAndKpi(Long employeId , Long kpiId) throws ResourceNotFound;

    Resource exportExcel() throws ResourceNotFound, IOException;
}
