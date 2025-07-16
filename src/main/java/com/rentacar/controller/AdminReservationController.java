package com.rentacar.controller;

import com.rentacar.model.Reservation;
import com.rentacar.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/reservations")
@RequiredArgsConstructor
@Slf4j
public class AdminReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public String listReservations(Model model) {
        List<Reservation> reservations = reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);
        log.info("Showing admin reservations list with {} items", reservations.size());
        return "admin/reservations/list";
    }

    @GetMapping("/{id}")
    public String viewReservation(@PathVariable Long id, Model model) {
        try {
            Reservation reservation = reservationService.getReservationById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid reservation Id: " + id));
            
            model.addAttribute("reservation", reservation);
            log.info("Showing admin reservation details for id: {}", id);
            return "admin/reservations/details";
        } catch (Exception e) {
            log.error("Error loading reservation details for id: {}", id, e);
            model.addAttribute("error", "Error loading reservation: " + e.getMessage());
            return "admin/reservations/details"; // Still return the template but with error message
        }
    }

    @PostMapping("/status/{id}")
    public String updateReservationStatus(
            @PathVariable Long id,
            @RequestParam Reservation.ReservationStatus status,
            RedirectAttributes redirectAttributes) {
        
        try {
            Reservation reservation;
            
            switch (status) {
                case CONFIRMED:
                    reservation = reservationService.confirmReservation(id);
                    break;
                case CANCELED:
                    reservation = reservationService.cancelReservation(id);
                    break;
                case COMPLETED:
                    reservation = reservationService.completeReservation(id);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status: " + status);
            }
            
            redirectAttributes.addFlashAttribute("message", 
                    "Reservation status updated to " + reservation.getStatus());
            log.info("Updated reservation {} status to {}", id, status);
        } catch (Exception e) {
            log.error("Error updating reservation status", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/reservations/" + id;
    }
}
