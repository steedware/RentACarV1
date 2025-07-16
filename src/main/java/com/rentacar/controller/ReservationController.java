package com.rentacar.controller;

import com.rentacar.model.Reservation;
import com.rentacar.model.User;
import com.rentacar.model.Vehicle;
import com.rentacar.service.ReservationService;
import com.rentacar.service.UserService;
import com.rentacar.service.VehicleService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.rentacar.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final VehicleService vehicleService;
    private final UserService userService;
    private final PaymentService paymentService;

    @GetMapping("/create/{vehicleId}")
    public String createReservationForm(
            @PathVariable Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Model model) {
        
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle Id: " + vehicleId));
        
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "reservations/create";
    }

    @PostMapping("/create")
    public String createReservation(
            @RequestParam Long vehicleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            RedirectAttributes redirectAttributes) {
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) userService.findByUsername(auth.getName());
            
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle Id: " + vehicleId));
            
            Reservation reservation = reservationService.createReservation(user, vehicle, startDate, endDate);
            
            return "redirect:/reservations/payment/" + reservation.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/vehicles/" + vehicleId;
        }
    }

    @GetMapping("/payment/{reservationId}")
    public String showPaymentPage(@PathVariable Long reservationId, Model model) {
        Reservation reservation = reservationService.getReservationById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation Id: " + reservationId));
        
        model.addAttribute("reservation", reservation);
        
        try {
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(reservation);
            model.addAttribute("clientSecret", paymentIntent.getClientSecret());
            model.addAttribute("stripePublicKey", "pk_test_your_stripe_public_key");
        } catch (StripeException e) {
            model.addAttribute("error", "Payment processing error: " + e.getMessage());
        }
        
        return "reservations/payment";
    }

    @PostMapping("/confirm-payment")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @RequestParam String paymentIntentId,
            @RequestParam Long reservationId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Reservation reservation = reservationService.getReservationById(reservationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid reservation Id: " + reservationId));
            
            paymentService.processPayment(reservation, paymentIntentId);
            
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/details/{id}")
    public String getReservationDetails(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.getReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation Id: " + id));
        
        model.addAttribute("reservation", reservation);
        return "reservations/details";
    }

    @PostMapping("/cancel/{id}")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.cancelReservation(id);
            redirectAttributes.addFlashAttribute("message", "Reservation cancelled successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/reservations";
    }

    @PostMapping("/rate/{id}")
    public String rateReservation(
            @PathVariable Long id,
            @RequestParam int rating,
            @RequestParam String feedback,
            RedirectAttributes redirectAttributes) {
        
        try {
            reservationService.rateReservation(id, rating, feedback);
            redirectAttributes.addFlashAttribute("message", "Rating submitted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/reservations/details/" + id;
    }
}
