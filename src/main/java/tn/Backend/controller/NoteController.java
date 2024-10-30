package tn.Backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.Backend.dto.NoteDto;
import tn.Backend.exception.ResourceNotFound;
import tn.Backend.services.NoteService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
@PreAuthorize("hasRole('AGENT')")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;
    @GetMapping("liste")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<List<NoteDto>> getAllNotes() {
        List<NoteDto> allNotes = noteService.getAllNotes();
        return ResponseEntity.ok(allNotes);
    }

    @PostMapping("/addNote")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<NoteDto> ajouterNote(@Valid @RequestBody NoteDto noteDto , Authentication authentication) throws ResourceNotFound {
        NoteDto createdNote = noteService.ajouterNote(noteDto , authentication);
        return ResponseEntity.ok(createdNote);
    }

    @GetMapping("/getNote/{employeId}/{kpiId}")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<NoteDto> getNote(@PathVariable Long employeId , @PathVariable Long kpiId   , Authentication authentication) throws ResourceNotFound {
        NoteDto createdNote = noteService.getNote(employeId ,kpiId, authentication);
        return ResponseEntity.ok(createdNote);
    }

    @GetMapping("/employe/{cin}")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<List<NoteDto>> obtenirNotesParEmployeCin(@PathVariable Long cin) {
        List<NoteDto> notes = noteService.obtenirNotesParEmployeCin(cin);
        return ResponseEntity.ok(notes);
    }

    @PutMapping("/updateNote/{id}")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<NoteDto> mettreAJourNote(@PathVariable Long id, @RequestBody NoteDto noteDto) throws ResourceNotFound {
        NoteDto updatedNote = noteService.mettreAJourNote(id, noteDto);
        return ResponseEntity.ok(updatedNote);
    }
    @PutMapping("/updateNoteByCin/{cin}")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<NoteDto> updateNoteByCin(@PathVariable Long cin, @RequestBody NoteDto noteDto) throws ResourceNotFound {
        NoteDto updatedNote = noteService.mettreAJourNoteByCin(cin, noteDto);
        return ResponseEntity.ok(updatedNote);
    }
    @DeleteMapping("/deleteByCin/{cin}")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<Void> deleteNoteByCin(@PathVariable Long cin) throws ResourceNotFound {
        noteService.supprimerNoteByCin(cin);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('AGENT')")
    public ResponseEntity<Void> supprimerNote(@PathVariable Long id) throws ResourceNotFound {
        noteService.supprimerNote(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasAuthority('AGENT')")
    @GetMapping("/exportExcel")
    public ResponseEntity<Resource> exportExcel() throws IOException, ResourceNotFound {

        Resource resource = noteService.exportExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Evaluation list.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }
}
